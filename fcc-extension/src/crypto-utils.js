const crypto = require('crypto');
let ethers;
try {
    ethers = require('ethers');
} catch (e) {
    ethers = require('../../contracts/node_modules/ethers');
}

/**
 * ECIES (Elliptic Curve Integrated Encryption Scheme) Implementation for Flare FCC TEE
 * Ephemeral secp256k1 Keypair + ECDH Shared Secret + HKDF (SHA-256) + AES-256-GCM
 */

function getPublicKeyFromPrivateKey(privateKeyHex) {
    const wallet = new ethers.Wallet(privateKeyHex);
    return wallet.signingKey.publicKey;
}

function encryptECIES(publicKeyHex, plaintextString) {
    const ephemeralWallet = ethers.Wallet.createRandom();
    const ephemeralPublicKey = ephemeralWallet.signingKey.publicKey; // 65 bytes uncompressed (0x04...)

    // Compute ECDH Shared Secret: Ephemeral Private Key * Recipient Public Key
    const sharedSecretHex = ephemeralWallet.signingKey.computeSharedSecret(publicKeyHex);
    const sharedSecretBuffer = Buffer.from(sharedSecretHex.substring(2), 'hex');

    // HKDF Derive Encryption Key (32 bytes) + IV (12 bytes)
    const hkdfRaw = crypto.hkdfSync('sha256', sharedSecretBuffer, Buffer.alloc(0), Buffer.from('XRPShield-FCC-ECIES-v1'), 44);
    const hkdfKey = Buffer.from(hkdfRaw);
    const aesKey = hkdfKey.subarray(0, 32);
    const iv = hkdfKey.subarray(32, 44);

    // AES-256-GCM Authenticated Encryption
    const cipher = crypto.createCipheriv('aes-256-gcm', aesKey, iv);
    const encryptedText = Buffer.concat([cipher.update(plaintextString, 'utf8'), cipher.final()]);
    const authTag = cipher.getAuthTag();

    // Payload: EphemeralPubKey (65 bytes) + IV (12 bytes) + AuthTag (16 bytes) + Ciphertext
    const payload = Buffer.concat([
        Buffer.from(ephemeralPublicKey.substring(2), 'hex'),
        iv,
        authTag,
        encryptedText
    ]);

    return '0x' + payload.toString('hex');
}

function decryptECIES(privateKeyHex, ciphertextHex) {
    const ciphertextBuffer = Buffer.from(ciphertextHex.startsWith('0x') ? ciphertextHex.substring(2) : ciphertextHex, 'hex');

    if (ciphertextBuffer.length < 93) { // 65 (PubKey) + 12 (IV) + 16 (Tag) = 93 bytes min
        throw new Error('Invalid ECIES ciphertext payload length');
    }

    const ephemeralPublicKeyHex = '0x' + ciphertextBuffer.subarray(0, 65).toString('hex');
    const iv = ciphertextBuffer.subarray(65, 77);
    const authTag = ciphertextBuffer.subarray(77, 93);
    const encryptedText = ciphertextBuffer.subarray(93);

    // Compute ECDH Shared Secret: Recipient Private Key * Ephemeral Public Key
    const wallet = new ethers.Wallet(privateKeyHex);
    const sharedSecretHex = wallet.signingKey.computeSharedSecret(ephemeralPublicKeyHex);
    const sharedSecretBuffer = Buffer.from(sharedSecretHex.substring(2), 'hex');

    // HKDF Derive Encryption Key (32 bytes) + IV (12 bytes)
    const hkdfRaw = crypto.hkdfSync('sha256', sharedSecretBuffer, Buffer.alloc(0), Buffer.from('XRPShield-FCC-ECIES-v1'), 44);
    const hkdfKey = Buffer.from(hkdfRaw);
    const aesKey = hkdfKey.subarray(0, 32);

    // AES-256-GCM Decryption
    const decipher = crypto.createDecipheriv('aes-256-gcm', aesKey, iv);
    decipher.setAuthTag(authTag);
    const decryptedText = Buffer.concat([decipher.update(encryptedText), decipher.final()]);

    return decryptedText.toString('utf8');
}

module.exports = {
    getPublicKeyFromPrivateKey,
    encryptECIES,
    decryptECIES
};
