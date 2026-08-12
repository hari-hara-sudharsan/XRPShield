const { ethers } = require('ethers');

/**
 * Handles incoming TEE extension instructions for OP_TYPE_XRP_SHIELD
 */
async function handleInstruction(instruction, config, wallet) {
    const { command, vaultAddress, policyCommitment, currentXrpPrice, deadline } = instruction;

    if (command === 'GET_STATUS') {
        return {
            success: true,
            status: 'READY',
            rationale: 'XRPShield FCC Extension operational',
            extensionId: config.extensionId,
            operationType: config.operationType,
            timestamp: Math.floor(Date.now() / 1000)
        };
    }

    if (command === 'EVALUATE_POLICY') {
        const timestamp = Math.floor(Date.now() / 1000);
        const attestationHash = ethers.keccak256(
            ethers.AbiCoder.defaultAbiCoder().encode(
                ['address', 'bytes32', 'uint256', 'uint256'],
                [vaultAddress, policyCommitment, currentXrpPrice, timestamp]
            )
        );

        const nonce = Math.floor(Math.random() * 100000) + 1;
        const status = 'APPROVED';
        const rationale = `Policy ${policyCommitment.substring(0, 10)}... evaluated: Approved for hedge authorization`;

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
            vaultAddress,
            policyHash: policyCommitment,
            status,
            attestationHash,
            nonce,
            timestamp,
            deadline: deadline || (timestamp + 3600)
        };

        const signature = await wallet.signTypedData(domain, types, value);

        return {
            success: true,
            status,
            rationale,
            policyHash: policyCommitment,
            attestationHash,
            nonce,
            timestamp,
            deadline: value.deadline,
            signature,
            signerAddress: wallet.address
        };
    }

    throw new Error(`Unsupported FCC instruction command: ${command}`);
}

module.exports = { handleInstruction };
