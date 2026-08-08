package com.xrpshield.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class Web3SignatureVerifier {

    private static final Logger logger = LoggerFactory.getLogger(Web3SignatureVerifier.class);
    private static final String PERSONAL_SIGN_PREFIX = "\u0019Ethereum Signed Message:\n";

    public boolean verifySignature(String address, String originalMessage, String signatureHex) {
        if (address == null || originalMessage == null || signatureHex == null) {
            return false;
        }

        try {
            byte[] msgBytes = originalMessage.getBytes(StandardCharsets.UTF_8);
            byte[] prefixBytes = (PERSONAL_SIGN_PREFIX + msgBytes.length).getBytes(StandardCharsets.UTF_8);

            byte[] prefixAndMsg = new byte[prefixBytes.length + msgBytes.length];
            System.arraycopy(prefixBytes, 0, prefixAndMsg, 0, prefixBytes.length);
            System.arraycopy(msgBytes, 0, prefixAndMsg, prefixBytes.length, msgBytes.length);

            byte[] signatureBytes = Numeric.hexStringToByteArray(signatureHex);
            if (signatureBytes.length < 65) {
                return false;
            }

            byte v = signatureBytes[64];
            if (v < 27) {
                v += 27;
            }

            Sign.SignatureData sd = new Sign.SignatureData(
                    v,
                    Arrays.copyOfRange(signatureBytes, 0, 32),
                    Arrays.copyOfRange(signatureBytes, 32, 64)
            );

            BigInteger publicKey = Sign.signedMessageToKey(prefixAndMsg, sd);
            String recoveredAddress = "0x" + Keys.getAddress(publicKey);

            boolean isMatch = address.equalsIgnoreCase(recoveredAddress);
            if (!isMatch) {
                logger.warn("Wallet signature mismatch! Expected: {}, Recovered: {}", address, recoveredAddress);
            }
            return isMatch;
        } catch (Exception e) {
            logger.error("Failed to verify wallet signature for address {}: {}", address, e.getMessage());
            return false;
        }
    }
}
