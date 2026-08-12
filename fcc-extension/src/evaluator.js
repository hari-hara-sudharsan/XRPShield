let ethers;
try {
    ethers = require('ethers');
} catch (e) {
    ethers = require('../../contracts/node_modules/ethers');
}
const { decryptECIES } = require('./crypto-utils');

/**
 * Evaluates private hedge policy inside Flare TEE Enclave runner.
 * Handles encrypted ECIES ciphertext payloads and evaluates trigger threshold, risk limits, vault balance, deadline, and price freshness.
 */
async function evaluatePrivateHedgePolicy(params, config, wallet) {
    const timestamp = params.timestamp || Math.floor(Date.now() / 1000);

    const vaultAddress = params.vaultAddress || params.vaultId;
    const contractAddress = params.contractAddress || config.coston2Network.teeRegistryAddress;
    const chainId = parseInt(params.chainId || config.coston2Network.chainId);
    const committedPolicyHash = params.policyCommitment || params.committedPolicyHash;

    const currentPrice = parseFloat(params.currentPrice || '1.00');
    const referencePrice = parseFloat(params.referencePrice || '1.15');
    const priceTimestamp = parseInt(params.priceTimestamp || timestamp);

    const vaultBalance = parseFloat(params.vaultBalance || '100000');
    const vaultStatus = params.vaultStatus || 'ACTIVE';

    let policy = params.policy;

    // Handle Encrypted ECIES Payload if encryptedCiphertext is provided
    if (params.encryptedCiphertext || params.ciphertext) {
        try {
            const ciphertext = params.encryptedCiphertext || params.ciphertext;
            const decryptedJsonStr = decryptECIES(wallet.privateKey, ciphertext);
            policy = JSON.parse(decryptedJsonStr);
            console.log('[FCC TEE Enclave] ECIES Ciphertext decrypted successfully inside TEE memory');
        } catch (err) {
            console.error('[FCC TEE Enclave] Decryption failed:', err.message);
            policy = null;
        }
    }

    if (!policy) {
        policy = {
            hedgeRatio: params.hedgeRatio || '1.0000',
            triggerThreshold: params.triggerThreshold || '10.0',
            maximumHedgeAmount: params.maximumHedgeAmount || params.maximumProtection || '100000.0',
            deadline: params.deadline !== undefined ? params.deadline : (timestamp + 86400),
            nonce: params.nonce !== undefined ? params.nonce : 9001,
            policyVersion: params.policyVersion || 1
        };
    }

    const hedgeRatio = parseFloat(policy.hedgeRatio || '1.0');
    const triggerThreshold = parseFloat(policy.triggerThreshold || '10.0');
    const maximumHedgeAmount = parseFloat(policy.maximumHedgeAmount || policy.maximumProtection || '100000.0');
    const deadline = parseInt(policy.deadline !== undefined ? policy.deadline : (timestamp + 86400));
    const nonce = parseInt(policy.nonce !== undefined ? policy.nonce : 9001);

    // Helper to format response
    const buildResult = async (status, rationale, approvedAmount = 0) => {
        const attestationHash = ethers.keccak256(
            ethers.AbiCoder.defaultAbiCoder().encode(
                ['address', 'bytes32', 'string', 'uint256', 'uint256'],
                [vaultAddress && ethers.isAddress(vaultAddress) ? vaultAddress : ethers.ZeroAddress, committedPolicyHash || ethers.ZeroHash, status, nonce, timestamp]
            )
        );

        const domain = {
            name: config.eip712Domain.name,
            version: config.eip712Domain.version,
            chainId: config.eip712Domain.chainId,
            verifyingContract: config.eip712Domain.verifyingContract
        };

        const types = {
            ActionResult: [
                { name: 'vaultAddress', type: 'address' },
                { name: 'policyHash', type: 'bytes32' },
                { name: 'status', type: 'string' },
                { name: 'attestationHash', type: 'bytes32' },
                { name: 'nonce', type: 'uint256' },
                { name: 'timestamp', type: 'uint256' },
                { name: 'deadline', type: 'uint256' }
            ]
        };

        const value = {
            vaultAddress: vaultAddress && ethers.isAddress(vaultAddress) ? vaultAddress : ethers.ZeroAddress,
            policyHash: committedPolicyHash || ethers.ZeroHash,
            status: status,
            attestationHash: attestationHash,
            nonce: nonce,
            timestamp: timestamp,
            deadline: deadline
        };

        const signature = await wallet.signTypedData(domain, types, value);

        return {
            success: status !== 'REJECTED',
            status: status,
            decision: status,
            rationale: rationale,
            policyCommitment: committedPolicyHash || ethers.ZeroHash,
            policyHash: committedPolicyHash || ethers.ZeroHash,
            vaultId: vaultAddress,
            vaultAddress: vaultAddress,
            approvedHedgeAmount: approvedAmount.toFixed(4),
            attestationHash: attestationHash,
            nonce: nonce,
            timestamp: timestamp,
            deadline: deadline,
            signature: signature,
            signerAddress: wallet.address
        };
    };

    // 1. Validate Contract Address & Chain ID
    if (!contractAddress || !ethers.isAddress(contractAddress) || contractAddress === ethers.ZeroAddress) {
        return buildResult('REJECTED', 'Invalid target contract address.');
    }
    if (chainId !== 114) {
        return buildResult('REJECTED', 'Chain ID mismatch. Execution allowed only on Flare Coston2 (Chain ID 114).');
    }

    // 2. Validate Vault Address
    if (!vaultAddress || !ethers.isAddress(vaultAddress) || vaultAddress === ethers.ZeroAddress) {
        return buildResult('REJECTED', 'Invalid vault ID or vault address.');
    }

    // 3. Validate Vault Status & Balance
    if (vaultStatus !== 'ACTIVE') {
        return buildResult('REJECTED', 'Vault is inactive or paused.');
    }
    if (vaultBalance <= 0) {
        return buildResult('REJECTED', 'Vault balance is zero or negative.');
    }

    // 4. Validate Policy Deadline & Nonce
    if (deadline > 0 && timestamp > deadline) {
        return buildResult('REJECTED', 'Policy execution deadline has expired.');
    }
    if (isNaN(nonce) || nonce <= 0) {
        return buildResult('REJECTED', 'Invalid policy nonce.');
    }

    // 5. Validate Price Freshness (<= 180 seconds)
    const freshnessSeconds = Math.max(0, timestamp - priceTimestamp);
    if (freshnessSeconds > 180) {
        return buildResult('REJECTED', `Price feed is stale (${freshnessSeconds}s old). Threshold limit is 180s.`);
    }

    // 6. Validate Policy Commitment Match
    if (committedPolicyHash) {
        const canonicalPayload = JSON.stringify({
            vaultAddress: vaultAddress ? vaultAddress.toLowerCase() : '0x0',
            asset: 'FXRP',
            hedgeRatio: policy.hedgeRatio || '1.0000',
            triggerThreshold: policy.triggerThreshold || '10.0',
            maximumProtection: policy.maximumProtection || policy.maximumHedgeAmount || '100000.0',
            deadline: policy.deadline !== undefined ? parseInt(policy.deadline) : deadline,
            nonce: policy.nonce !== undefined ? parseInt(policy.nonce) : nonce,
            policyVersion: parseInt(policy.policyVersion || 1)
        });

        const candidateHash = ethers.keccak256(ethers.toUtf8Bytes(canonicalPayload));
        if (committedPolicyHash.toLowerCase() !== candidateHash.toLowerCase()) {
            return buildResult('REJECTED', 'Candidate policy parameters do not match active on-chain policy commitment hash.');
        }
    }

    // 7. Evaluate Trigger Threshold Math
    const priceDropPercent = referencePrice > 0 ? ((referencePrice - currentPrice) / referencePrice) * 100 : 0;

    if (priceDropPercent >= triggerThreshold) {
        const uncappedHedge = vaultBalance * hedgeRatio;
        const approvedHedgeAmount = Math.min(uncappedHedge, maximumHedgeAmount);
        const rationale = `POLICY TRIGGERED: FTSOv2 price drop of ${priceDropPercent.toFixed(2)}% crossed ${triggerThreshold}% threshold. Approved hedge of ${approvedHedgeAmount.toFixed(2)} FXRP.`;
        return buildResult('APPROVED', rationale, approvedHedgeAmount);
    } else {
        const rationale = `NO ACTION: Market price drop of ${priceDropPercent.toFixed(2)}% did not cross ${triggerThreshold}% threshold.`;
        return buildResult('NO_ACTION', rationale, 0);
    }
}

module.exports = { evaluatePrivateHedgePolicy };
