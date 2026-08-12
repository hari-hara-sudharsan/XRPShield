const assert = require('assert');
let ethers;
try {
    ethers = require('ethers');
} catch (e) {
    ethers = require('../../contracts/node_modules/ethers');
}
const config = require('../config/extension-config.json');
const { evaluatePrivateHedgePolicy } = require('../src/evaluator');

const wallet = ethers.Wallet.createRandom();

async function runTests() {
    console.log('\n========================================================');
    console.log('  Running XRPShield TEE Policy Evaluator Test Suite    ');
    console.log('========================================================\n');

    const validVault = ethers.Wallet.createRandom().address;
    const validContract = config.coston2Network.teeRegistryAddress;
    const now = Math.floor(Date.now() / 1000);

    const basePolicy = {
        hedgeRatio: '1.0000',
        triggerThreshold: '10.0',
        maximumHedgeAmount: '50000.0',
        deadline: now + 3600,
        nonce: 9001,
        policyVersion: 1
    };

    const canonicalStr = JSON.stringify({
        vaultAddress: validVault.toLowerCase(),
        asset: 'FXRP',
        hedgeRatio: basePolicy.hedgeRatio,
        triggerThreshold: basePolicy.triggerThreshold,
        maximumProtection: basePolicy.maximumHedgeAmount,
        deadline: basePolicy.deadline,
        nonce: basePolicy.nonce,
        policyVersion: basePolicy.policyVersion
    });

    const validCommitment = ethers.keccak256(ethers.toUtf8Bytes(canonicalStr));

    // Test 1: APPROVED Scenario (Price drop 15% >= 10% threshold)
    {
        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: validCommitment,
            currentPrice: '0.85',
            referencePrice: '1.00',
            priceTimestamp: now,
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE',
            policy: basePolicy
        }, config, wallet);

        assert.strictEqual(res.status, 'APPROVED', 'Test 1 Failed: Expected APPROVED');
        assert.strictEqual(res.approvedHedgeAmount, '50000.0000', 'Test 1 Failed: Expected max hedge cap 50000.0000');
        assert.ok(res.signature, 'Test 1 Failed: Expected valid EIP-712 signature');
        console.log('✅ 1. APPROVED Scenario (Price drop 15% >= 10% threshold): PASSED');
    }

    // Test 2: NO_ACTION Scenario (Price drop 5% < 10% threshold)
    {
        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: validCommitment,
            currentPrice: '0.95',
            referencePrice: '1.00',
            priceTimestamp: now,
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE',
            policy: basePolicy
        }, config, wallet);

        assert.strictEqual(res.status, 'NO_ACTION', 'Test 2 Failed: Expected NO_ACTION');
        assert.strictEqual(res.approvedHedgeAmount, '0.0000', 'Test 2 Failed: Expected 0 hedge amount');
        console.log('✅ 2. NO_ACTION Scenario (Price drop 5% < 10% threshold): PASSED');
    }

    // Test 3: REJECTED Scenario (Vault inactive)
    {
        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: validCommitment,
            currentPrice: '0.85',
            referencePrice: '1.00',
            priceTimestamp: now,
            vaultBalance: '100000',
            vaultStatus: 'PAUSED',
            policy: basePolicy
        }, config, wallet);

        assert.strictEqual(res.status, 'REJECTED', 'Test 3 Failed: Expected REJECTED');
        console.log('✅ 3. REJECTED Scenario (Inactive vault): PASSED');
    }

    // Test 4: Expired Policy Scenario
    {
        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: validCommitment,
            currentPrice: '0.85',
            referencePrice: '1.00',
            priceTimestamp: now,
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE',
            policy: { ...basePolicy, deadline: now - 600 }
        }, config, wallet);

        assert.strictEqual(res.status, 'REJECTED', 'Test 4 Failed: Expected REJECTED for expired policy');
        console.log('✅ 4. Expired Policy Scenario: PASSED');
    }

    // Test 5: Invalid Policy Commitment Match Scenario
    {
        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: ethers.keccak256(ethers.toUtf8Bytes('wrong-hash')),
            currentPrice: '0.85',
            referencePrice: '1.00',
            priceTimestamp: now,
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE',
            policy: basePolicy
        }, config, wallet);

        assert.strictEqual(res.status, 'REJECTED', 'Test 5 Failed: Expected REJECTED for commitment mismatch');
        console.log('✅ 5. Invalid Policy Commitment Match Scenario: PASSED');
    }

    // Test 6: Invalid Nonce Scenario
    {
        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: validCommitment,
            currentPrice: '0.85',
            referencePrice: '1.00',
            priceTimestamp: now,
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE',
            policy: { ...basePolicy, nonce: 0 }
        }, config, wallet);

        assert.strictEqual(res.status, 'REJECTED', 'Test 6 Failed: Expected REJECTED for invalid nonce');
        console.log('✅ 6. Invalid Nonce Scenario: PASSED');
    }

    // Test 7: Invalid Vault Address Scenario
    {
        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: '0x0000000000000000000000000000000000000000',
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: validCommitment,
            currentPrice: '0.85',
            referencePrice: '1.00',
            priceTimestamp: now,
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE',
            policy: basePolicy
        }, config, wallet);

        assert.strictEqual(res.status, 'REJECTED', 'Test 7 Failed: Expected REJECTED for zero vault address');
        console.log('✅ 7. Invalid Vault Address Scenario: PASSED');
    }

    // Test 8: Stale Price Feed (> 180s old) Scenario
    {
        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: validCommitment,
            currentPrice: '0.85',
            referencePrice: '1.00',
            priceTimestamp: now - 300, // 5 minutes old
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE',
            policy: basePolicy
        }, config, wallet);

        assert.strictEqual(res.status, 'REJECTED', 'Test 8 Failed: Expected REJECTED for stale price feed');
        console.log('✅ 8. Stale Price Feed (> 180s old) Scenario: PASSED');
    }

    // Test 9: Excessive Hedge Cap Enforcement Scenario
    {
        const smallCapPolicy = { ...basePolicy, maximumHedgeAmount: '10000.0' };
        const smallCapCommitment = ethers.keccak256(ethers.toUtf8Bytes(JSON.stringify({
            vaultAddress: validVault.toLowerCase(),
            asset: 'FXRP',
            hedgeRatio: smallCapPolicy.hedgeRatio,
            triggerThreshold: smallCapPolicy.triggerThreshold,
            maximumProtection: smallCapPolicy.maximumHedgeAmount,
            deadline: smallCapPolicy.deadline,
            nonce: smallCapPolicy.nonce,
            policyVersion: smallCapPolicy.policyVersion
        })));

        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: smallCapCommitment,
            currentPrice: '0.85',
            referencePrice: '1.00',
            priceTimestamp: now,
            vaultBalance: '100000', // Uncapped would be 100,000, but cap is 10,000
            vaultStatus: 'ACTIVE',
            policy: smallCapPolicy
        }, config, wallet);

        assert.strictEqual(res.status, 'APPROVED', 'Test 9 Failed: Expected APPROVED');
        assert.strictEqual(res.approvedHedgeAmount, '10000.0000', 'Test 9 Failed: Expected capped amount 10000.0000');
        console.log('✅ 9. Excessive Hedge Cap Enforcement Scenario: PASSED');
    }

    // Test 10: Invalid Contract Address Scenario
    {
        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: '0x0000000000000000000000000000000000000000',
            chainId: 114,
            policyCommitment: validCommitment,
            currentPrice: '0.85',
            referencePrice: '1.00',
            priceTimestamp: now,
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE',
            policy: basePolicy
        }, config, wallet);

        assert.strictEqual(res.status, 'REJECTED', 'Test 10 Failed: Expected REJECTED for zero contract address');
        console.log('✅ 10. Invalid Contract Address Scenario: PASSED');
    }

    console.log('\n🎉 ALL 10 TEE EVALUATOR TEST SCENARIOS PASSED SUCCESSFULLY!');
    console.log('========================================================\n');
}

runTests().catch(err => {
    console.error('❌ Evaluator Test Suite Failed:', err);
    process.exit(1);
});
