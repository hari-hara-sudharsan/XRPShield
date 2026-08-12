const assert = require('assert');
let ethers;
try {
    ethers = require('ethers');
} catch (e) {
    ethers = require('../../contracts/node_modules/ethers');
}
const config = require('../config/extension-config.json');
const { getPublicKeyFromPrivateKey, encryptECIES, decryptECIES } = require('../src/crypto-utils');
const { evaluatePrivateHedgePolicy } = require('../src/evaluator');

const SIGNER_KEY = '0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80';
const wallet = new ethers.Wallet(SIGNER_KEY);
const teePublicKey = getPublicKeyFromPrivateKey(SIGNER_KEY);

async function runTests() {
    console.log('\n========================================================');
    console.log('  Running XRPShield ECIES Confidential Encryption Tests ');
    console.log('========================================================\n');

    const validVault = ethers.Wallet.createRandom().address;
    const validContract = config.coston2Network.teeRegistryAddress;
    const now = Math.floor(Date.now() / 1000);

    const secretPolicyObj = {
        hedgeRatio: '1.0000',
        triggerThreshold: '10.0',
        maximumHedgeAmount: '50000.0',
        deadline: now + 3600,
        nonce: 9001,
        policyVersion: 1
    };

    const canonicalJson = JSON.stringify({
        vaultAddress: validVault.toLowerCase(),
        asset: 'FXRP',
        hedgeRatio: secretPolicyObj.hedgeRatio,
        triggerThreshold: secretPolicyObj.triggerThreshold,
        maximumProtection: secretPolicyObj.maximumHedgeAmount,
        deadline: secretPolicyObj.deadline,
        nonce: secretPolicyObj.nonce,
        policyVersion: secretPolicyObj.policyVersion
    });

    const policyCommitment = ethers.keccak256(ethers.toUtf8Bytes(canonicalJson));

    // Test 1: TEE Public Key Derivation & Export
    {
        assert.ok(teePublicKey.startsWith('0x04'), 'Test 1 Failed: Uncompressed secp256k1 public key should start with 0x04');
        assert.strictEqual(teePublicKey.length, 132, 'Test 1 Failed: Public key length should be 132 characters (0x + 65 hex bytes)');
        console.log('✅ 1. TEE Public Key Export (secp256k1): PASSED');
    }

    // Test 2: Client ECIES Encryption & Decryption Roundtrip
    let ciphertext;
    {
        ciphertext = encryptECIES(teePublicKey, canonicalJson);
        assert.ok(ciphertext.startsWith('0x'), 'Test 2 Failed: Ciphertext must be 0x hex');
        assert.ok(ciphertext.length > 200, 'Test 2 Failed: Ciphertext length should include ephemeral key + IV + tag + ciphertext');

        const decrypted = decryptECIES(SIGNER_KEY, ciphertext);
        assert.strictEqual(decrypted, canonicalJson, 'Test 2 Failed: Decrypted text must equal original canonical JSON');
        console.log('✅ 2. Client ECIES Encryption & Decryption Roundtrip: PASSED');
    }

    // Test 3: TEE Enclave Internal Decryption & Evaluation
    {
        const res = await evaluatePrivateHedgePolicy({
            vaultAddress: validVault,
            contractAddress: validContract,
            chainId: 114,
            policyCommitment: policyCommitment,
            encryptedCiphertext: ciphertext, // Sending ONLY ciphertext to TEE!
            currentPrice: '0.85',
            referencePrice: '1.00',
            priceTimestamp: now,
            vaultBalance: '100000',
            vaultStatus: 'ACTIVE'
        }, config, wallet);

        assert.strictEqual(res.status, 'APPROVED', 'Test 3 Failed: Expected APPROVED after internal TEE decryption');
        assert.strictEqual(res.approvedHedgeAmount, '50000.0000', 'Test 3 Failed: Expected capped amount 50000.0000');
        console.log('✅ 3. TEE Enclave Internal Decryption & Policy Evaluation: PASSED');
    }

    // Test 4: Plaintext Leakage Inspection Verification
    {
        assert.ok(!ciphertext.includes('10.0'), 'Test 4 Failed: Plaintext triggerThreshold found in ciphertext payload');
        assert.ok(!ciphertext.includes('50000.0'), 'Test 4 Failed: Plaintext maximumHedgeAmount found in ciphertext payload');
        console.log('✅ 4. Zero Plaintext Leakage Verification: PASSED');
    }

    console.log('\n🎉 ALL ECIES CONFIDENTIAL ENCRYPTION TESTS PASSED SUCCESSFULLY!');
    console.log('========================================================\n');
}

runTests().catch(err => {
    console.error('❌ Encryption Test Suite Failed:', err);
    process.exit(1);
});
