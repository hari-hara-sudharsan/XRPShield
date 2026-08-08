package com.xrpshield.fcc;

import com.xrpshield.exception.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class PolicyEncryptionService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyEncryptionService.class);
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;

    private final SecretKey secretKey;

    public PolicyEncryptionService(@Value("${xrpshield.fcc.master-key:XRPShieldMasterEncryptionKey32B!}") String secretKeyString) {
        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        byte[] validKeyBytes = new byte[32];
        System.arraycopy(keyBytes, 0, validKeyBytes, 0, Math.min(keyBytes.length, 32));
        this.secretKey = new SecretKeySpec(validKeyBytes, "AES");
    }

    public EncryptedPayload encrypt(String plaintext) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            String ivBase64 = Base64.getEncoder().encodeToString(iv);
            String cipherBase64 = Base64.getEncoder().encodeToString(cipherText);
            String authTagBase64 = cipherBase64.length() > 16 ? cipherBase64.substring(cipherBase64.length() - 16) : "TAG_OK";

            return new EncryptedPayload(cipherBase64, ivBase64, authTagBase64);
        } catch (Exception e) {
            logger.error("Failed to encrypt confidential policy payload: {}", e.getMessage());
            throw new CryptoException("Policy payload encryption failed", e);
        }
    }

    public String decrypt(EncryptedPayload payload) {
        try {
            byte[] iv = Base64.getDecoder().decode(payload.getIv());
            byte[] cipherText = Base64.getDecoder().decode(payload.getEncryptedPayload());

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] plainTextBytes = cipher.doFinal(cipherText);
            return new String(plainTextBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Failed to decrypt confidential policy payload: {}", e.getMessage());
            throw new CryptoException("Policy payload decryption failed", e);
        }
    }

    public static class EncryptedPayload {
        private final String encryptedPayload;
        private final String iv;
        private final String authTag;

        public EncryptedPayload(String encryptedPayload, String iv, String authTag) {
            this.encryptedPayload = encryptedPayload;
            this.iv = iv;
            this.authTag = authTag;
        }

        public String getEncryptedPayload() {
            return encryptedPayload;
        }

        public String getIv() {
            return iv;
        }

        public String getAuthTag() {
            return authTag;
        }
    }
}
