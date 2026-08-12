const express = require('express');
let ethers;
try {
    ethers = require('ethers');
} catch (e) {
    ethers = require('../../contracts/node_modules/ethers');
}
const config = require('../config/extension-config.json');
const { handleInstruction } = require('./handler');
const { getPublicKeyFromPrivateKey } = require('./crypto-utils');

const app = express();
app.use(express.json());

const PORT = process.env.PORT || 8080;
const SIMULATED_TEE = process.env.SIMULATED_TEE !== 'false';
const SIGNER_KEY = process.env.EXTENSION_SIGNER_KEY || '0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80';

const wallet = new ethers.Wallet(SIGNER_KEY);
const teePublicKey = getPublicKeyFromPrivateKey(SIGNER_KEY);

console.log('\n========================================================');
console.log('  Starting XRPShield Flare Confidential Compute Server  ');
console.log('========================================================');
console.log('Extension ID:', config.extensionId);
console.log('Operation Type:', config.operationType);
console.log('TEE Signer Address:', wallet.address);
console.log('TEE Public Key (secp256k1):', teePublicKey);
console.log('Development Mode SIMULATED_TEE:', SIMULATED_TEE);
console.log('Target Chain ID:', config.coston2Network.chainId);
console.log('========================================================\n');

app.get('/health', (req, res) => {
    res.json({
        status: 'UP',
        simulatedTee: SIMULATED_TEE,
        extensionId: config.extensionId,
        signerAddress: wallet.address,
        publicKey: teePublicKey,
        timestamp: new Date().toISOString()
    });
});

app.get('/public-key', (req, res) => {
    res.json({
        success: true,
        extensionId: config.extensionId,
        publicKey: teePublicKey,
        algorithm: 'secp256k1-ECIES-GCM',
        signerAddress: wallet.address
    });
});

app.post('/instruction', async (req, res) => {
    try {
        const instruction = req.body;
        console.log(`[FCC TEE] Received instruction command: ${instruction.command}`);

        const result = await handleInstruction(instruction, config, wallet);
        res.json({ success: true, data: result });
    } catch (err) {
        console.error('[FCC TEE] Error processing instruction:', err.message);
        res.status(400).json({ success: false, error: err.message });
    }
});

if (require.main === module) {
    app.listen(PORT, () => {
        console.log(`🚀 XRPShield FCC Extension Server running on port ${PORT}`);
    });
}

module.exports = app;
