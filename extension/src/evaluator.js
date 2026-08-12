const { ethers } = require('../../contracts/node_modules/ethers');
const config = require('./config');

const wallet = new ethers.Wallet(config.SIGNER_PRIVATE_KEY);

/**
 * Evaluates private hedge policy strictly inside Flare TEE Enclave runner.
 */
async function evaluatePrivateHedgePolicy(params) {
    const timestamp = Math.floor(Date.now() / 1000);

    const vaultAddress = params.vaultAddress;
    const committedPolicyHash = params.committedPolicyHash;
    const currentPrice = parseFloat(params.currentPrice || '1.0225');
    const referencePrice = parseFloat(params.referencePrice || '1.1500');
    const vaultBalance = parseFloat(params.vaultBalance || '100000');
    const vaultStatus = params.vaultStatus || 'ACTIVE';

    const policy = params.policy || {
        hedgeRatio: '1.0000',
        triggerThreshold: '10.0',
        maximumProtection: '100000.0',
        deadline: timestamp + 86400,
        nonce: Date.now(),
        policyVersion: 1
    };

    const hedgeRatio = parseFloat(policy.hedgeRatio || '1.0');
    const triggerThreshold = parseFloat(policy.triggerThreshold || '10.0');
    const maximumProtection = parseFloat(policy.maximumProtection || '100000.0');
    const deadline = parseInt(policy.deadline || (timestamp + 86400));
    const nonce = parseInt(policy.nonce || Date.now());

    // 1. Verify Deadline Expiration
    if (deadline > 0 && timestamp > deadline) {
        return {
            success: false,
            error: 'POLICY_EXPIRED',
            status: 'REJECTED',
            rationale: 'Confidential policy deadline has expired.',
            evaluatedAt: timestamp
        };
    }

    // 2. Verify Vault Balance & Status
    if (vaultBalance <= 0) {
        return {
            success: false,
            error: 'INSUFFICIENT_BALANCE',
            status: 'REJECTED',
            rationale: 'Vault balance is zero or insufficient for hedging.',
            evaluatedAt: timestamp
        };
    }

    if (vaultStatus !== 'ACTIVE') {
        return {
            success: false,
            error: 'VAULT_INACTIVE',
            status: 'REJECTED',
            rationale: 'Vault is paused or deactivated.',
            evaluatedAt: timestamp
        };
    }

    // 3. Verify Deterministic Keccak256 Policy Commitment Match
    const canonicalPayload = JSON.stringify({
        vaultAddress: vaultAddress ? vaultAddress.toLowerCase() : '0x0',
        asset: 'FXRP',
        hedgeRatio: policy.hedgeRatio || '1.0000',
        triggerThreshold: policy.triggerThreshold || '10.0',
        maximumProtection: policy.maximumProtection || '100000.0',
        deadline: deadline,
        nonce: nonce,
        policyVersion: parseInt(policy.policyVersion || 1)
    });

    const candidateHash = ethers.keccak256(ethers.toUtf8Bytes(canonicalPayload));
    if (committedPolicyHash && committedPolicyHash.toLowerCase() !== candidateHash.toLowerCase()) {
        return {
            success: false,
            error: 'POLICY_COMMITMENT_MISMATCH',
            status: 'REJECTED',
            rationale: 'Candidate policy parameters do not match active on-chain policy commitment hash.',
            evaluatedAt: timestamp
        };
    }

    // 4. Perform Private Trigger Condition Evaluation inside TEE
    const priceDropPercent = referencePrice > 0 ? ((referencePrice - currentPrice) / referencePrice) * 100 : 0;
    let decision = 'NO_ACTION';
    let approvedHedgeAmount = 0;
    let rationale = `Market price movement (-${priceDropPercent.toFixed(2)}%) did not cross trigger threshold (${triggerThreshold}%). No action required.`;

    if (priceDropPercent >= triggerThreshold) {
        decision = 'APPROVED';
        approvedHedgeAmount = Math.min(vaultBalance * hedgeRatio, maximumProtection);
        rationale = `CONFIDENTIAL POLICY TRIGGERED: FTSOv2 price drop of ${priceDropPercent.toFixed(2)}% crossed ${triggerThreshold}% threshold. Approved hedge of ${approvedHedgeAmount.toFixed(2)} FXRP.`;
    }

    // Generate cryptographic attestation proof hash
    const attestationPayload = ethers.solidityPacked(
        ['address', 'bytes32', 'string', 'uint256', 'uint256'],
        [vaultAddress, candidateHash, decision, Math.floor(approvedHedgeAmount * 1e18), timestamp]
    );
    const attestationHash = ethers.keccak256(attestationPayload);

    // Sign EIP-712 ActionResult payload
    const domain = {
        name: "XRPShield FCC Extension",
        version: "1",
        chainId: config.CHAIN_ID,
        verifyingContract: "0x0000000000000000000000000000000000000000"
    };

    const types = {
        ActionResult: [
            { name: "vaultAddress", type: "address" },
            { name: "policyHash", type: "bytes32" },
            { name: "status", type: "string" },
            { name: "attestationHash", type: "bytes32" },
            { name: "nonce", type: "uint256" },
            { name: "timestamp", type: "uint256" },
            { name: "deadline", type: "uint256" }
        ]
    };

    const value = {
        vaultAddress: vaultAddress || '0x0000000000000000000000000000000000000000',
        policyHash: candidateHash,
        status: decision,
        attestationHash: attestationHash,
        nonce: nonce,
        timestamp: timestamp,
        deadline: deadline
    };

    const signature = await wallet.signTypedData(domain, types, value);

    return {
        success: true,
        status: decision,
        decision: decision,
        approvedHedgeAmount: approvedHedgeAmount,
        rationale: rationale,
        policyHash: candidateHash,
        attestationHash: attestationHash,
        nonce: nonce,
        evaluatedAt: timestamp,
        deadline: deadline,
        signerAddress: wallet.address,
        signature: signature
    };
}

function getExtensionStatus() {
    return {
        status: 'ACTIVE',
        service: 'Flare Confidential Compute Extension',
        network: 'Flare Coston2 Testnet',
        chainId: config.CHAIN_ID,
        signerAddress: wallet.address,
        ftsoV2Address: config.FTSOV2_CONTRACT_ADDRESS,
        timestamp: new Date().toISOString()
    };
}

module.exports = {
    evaluatePrivateHedgePolicy,
    getExtensionStatus
};
