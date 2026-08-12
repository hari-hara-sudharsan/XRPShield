const assert = require('assert');
let ethers;
try {
    ethers = require('ethers');
} catch (e) {
    ethers = require('../../contracts/node_modules/ethers');
}
const config = require('../config/extension-config.json');
const { getPublicKeyFromPrivateKey, encryptECIES } = require('../src/crypto-utils');
const { evaluatePrivateHedgePolicy } = require('../src/evaluator');

const SIGNER_KEY = '0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80';
const wallet = new ethers.Wallet(SIGNER_KEY);
const teePublicKey = getPublicKeyFromPrivateKey(SIGNER_KEY);

async function runTests() {
    console.log('\n========================================================');
    console.log('  Running Real TEE Financial Policy Evaluation Tests    ');
    console.log('========================================================\n');

    const validVault = ethers.Wallet.createRandom().address;
    const validContract = config.coston2Network.teeRegistryAddress;
    const fixedTimestamp = 1786540000;

    const privatePolicy = {
        hedgeRatio: '0.8000',
        triggerThreshold: '10.0',
        maximumHedgeAmount: '40000.0',
        deadline: fixedTimestamp + 3600,
        nonce: 10050,
        policyVersion: 1
    };

    const canonicalJson = JSON.stringify({
        vaultAddress: validVault.toLowerCase(),
        asset: 'FXRP',
        hedgeRatio: privatePolicy.hedgeRatio,
        triggerThreshold: privatePolicy.triggerThreshold,
        maximumProtection: privatePolicy.maximumHedgeAmount,
        deadline: privatePolicy.deadline,
        nonce: privatePolicy.nonce,
        policyVersion: privatePolicy.policyVersion
    });

    const policyCommitment = ethers.keccak256(ethers.toUtf8Bytes(canonicalJson));
    const encryptedCiphertext = encryptECIES(teePublicKey, canonicalJson);

    // Test 1: APPROVED Decision & Exact TEE Hedge Calculation
    // 100,000 vault balance * 0.80 hedge ratio = 80,000 uncapped. Max cap is 40,000 -> Approved 40000.0000
    {
        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: policyCommitment,
            encryptedCiphertext: encryptedCiphertext,
            currentPrice: '0.85', // 15% drop >= 10% threshold
            referencePrice: '1.00',
            priceTimestamp: fixedTimestamp,
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE',
            timestamp: fixedTimestamp
        }, config, wallet);

        assert.strictEqual(res.status, 'APPROVED', 'Test 1 Failed: Expected APPROVED decision');
        assert.strictEqual(res.approvedHedgeAmount, '40000.0000', 'Test 1 Failed: Expected exact TEE hedge amount 40000.0000');
        assert.strictEqual(res.vaultAddress, validVault, 'Test 1 Failed: Vault address mismatch');
        assert.ok(res.signature, 'Test 1 Failed: Expected valid EIP-712 signature');
        console.log('✅ 1. APPROVED Decision & Exact TEE Hedge Math (40,000.0000 FXRP): PASSED');
    }

    // Test 2: NO_ACTION Decision when Price Drop < Threshold %
    {
        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: policyCommitment,
            encryptedCiphertext: encryptedCiphertext,
            currentPrice: '0.95', // 5% drop < 10% threshold
            referencePrice: '1.00',
            priceTimestamp: fixedTimestamp,
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE',
            timestamp: fixedTimestamp
        }, config, wallet);

        assert.strictEqual(res.status, 'NO_ACTION', 'Test 2 Failed: Expected NO_ACTION decision');
        assert.strictEqual(res.approvedHedgeAmount, '0.0000', 'Test 2 Failed: Expected 0 hedge amount');
        console.log('✅ 2. NO_ACTION Decision when Price Drop < Threshold %: PASSED');
    }

    // Test 3: REJECTED Decision on Stale Price Feed
    {
        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: policyCommitment,
            encryptedCiphertext: encryptedCiphertext,
            currentPrice: '0.85',
            referencePrice: '1.00',
            priceTimestamp: fixedTimestamp - 500, // 500s old > 180s limit
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE',
            timestamp: fixedTimestamp
        }, config, wallet);

        assert.strictEqual(res.status, 'REJECTED', 'Test 3 Failed: Expected REJECTED decision on stale price');
        assert.strictEqual(res.approvedHedgeAmount, '0.0000', 'Test 3 Failed: Expected 0 hedge amount');
        console.log('✅ 3. REJECTED Decision on Stale Price Feed (> 180s old): PASSED');
    }

    // Test 4: Determinism Check (Identical inputs produce identical EIP-712 attestation hashes)
    {
        const resA = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: policyCommitment,
            encryptedCiphertext: encryptedCiphertext,
            currentPrice: '0.85',
            referencePrice: '1.00',
            priceTimestamp: fixedTimestamp,
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE',
            timestamp: fixedTimestamp
        }, config, wallet);

        const resB = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: policyCommitment,
            encryptedCiphertext: encryptedCiphertext,
            currentPrice: '0.85',
            referencePrice: '1.00',
            priceTimestamp: fixedTimestamp,
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE',
            timestamp: fixedTimestamp
        }, config, wallet);

        assert.strictEqual(resA.attestationHash, resB.attestationHash, 'Test 4 Failed: Attestation hashes must match identically');
        assert.strictEqual(resA.signature, resB.signature, 'Test 4 Failed: EIP-712 signatures must match identically');
        console.log('✅ 4. Deterministic Output & EIP-712 Signature Match: PASSED');
    }

    // Test 5: Sanitized Log Output Check (No secret parameters in result object)
    {
        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: policyCommitment,
            encryptedCiphertext: encryptedCiphertext,
            currentPrice: '0.85',
            referencePrice: '1.00',
            priceTimestamp: fixedTimestamp,
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE',
            timestamp: fixedTimestamp
        }, config, wallet);

        const keys = Object.keys(res);
        assert.ok(!keys.includes('triggerThreshold'), 'Test 5 Failed: Secret triggerThreshold found in result object');
        assert.ok(!keys.includes('hedgeRatio'), 'Test 5 Failed: Secret hedgeRatio found in result object');
        console.log('✅ 5. Sanitized Output Log Inspection (Zero Secret Leakage): PASSED');
    }

    console.log('\n🎉 ALL REAL TEE EVALUATION TESTS PASSED SUCCESSFULLY!');
    console.log('========================================================\n');
}

runTests().catch(err => {
    console.error('❌ Real TEE Evaluation Test Failed:', err);
    process.exit(1);
});
