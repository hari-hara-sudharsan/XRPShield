const express = require('express');
const cors = require('cors');
const config = require('./config');
const { evaluatePrivateHedgePolicy, getExtensionStatus } = require('./evaluator');

const app = express();
app.use(cors());
app.use(express.json());

// Log incoming requests
app.use((req, res, next) => {
    console.log(`[FCC-EXTENSION] ${req.method} ${req.path} - ${new Date().toISOString()}`);
    next();
});

// GET_EXTENSION_STATUS Command
app.get('/status', (req, res) => {
    const statusData = getExtensionStatus();
    res.json({
        success: true,
        command: 'GET_EXTENSION_STATUS',
        data: statusData
    });
});

// EVALUATE_HEDGE_POLICY Command
app.post('/evaluate-policy', async (req, res) => {
    try {
        const actionResult = await evaluatePrivateHedgePolicy(req.body);

        if (!actionResult.success) {
            console.warn(`[FCC-EXTENSION] Policy Evaluation Rejected: ${actionResult.error} - ${actionResult.rationale}`);
            return res.status(400).json({
                success: false,
                command: 'EVALUATE_HEDGE_POLICY',
                error: actionResult.error,
                rationale: actionResult.rationale,
                data: actionResult
            });
        }

        console.log(`[FCC-EXTENSION] Evaluated policy for ${req.body.vaultAddress} -> Decision: ${actionResult.decision}`);

        res.json({
            success: true,
            command: 'EVALUATE_HEDGE_POLICY',
            data: actionResult
        });
    } catch (err) {
        console.error('[FCC-EXTENSION] Policy Evaluation Error:', err);
        res.status(500).json({
            success: false,
            error: 'Confidential policy evaluation failed: ' + err.message
        });
    }
});

app.listen(config.PORT, () => {
    console.log(`=======================================================`);
    console.log(` XRPShield Flare Compute Extension Active on Port ${config.PORT}`);
    console.log(` Network Target: Flare Coston2 Testnet (Chain ID ${config.CHAIN_ID})`);
    console.log(` TEE Signer Address: ${getExtensionStatus().signerAddress}`);
    console.log(`=======================================================`);
});
