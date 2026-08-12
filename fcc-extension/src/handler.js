const { evaluatePrivateHedgePolicy } = require('./evaluator');

/**
 * Handles incoming TEE extension instructions for OP_TYPE_XRP_SHIELD
 */
async function handleInstruction(instruction, config, wallet) {
    const { command } = instruction;

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
        return await evaluatePrivateHedgePolicy(instruction, config, wallet);
    }

    throw new Error(`Unsupported FCC instruction command: ${command}`);
}

module.exports = { handleInstruction };
