package com.xrpshield.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Web3SignatureVerifierTest {

    private Web3SignatureVerifier verifier;

    @BeforeEach
    void setUp() {
        verifier = new Web3SignatureVerifier();
    }

    @Test
    @DisplayName("Should return false when arguments are null")
    void testNullArguments() {
        assertFalse(verifier.verifySignature(null, "message", "0x123"));
        assertFalse(verifier.verifySignature("0x123", null, "0x123"));
        assertFalse(verifier.verifySignature("0x123", "message", null));
    }

    @Test
    @DisplayName("Should return false for invalid signature format")
    void testInvalidSignatureFormat() {
        boolean result = verifier.verifySignature(
                "0x1111111111111111111111111111111111111111",
                "Sign-In With Flare: Nonce=12345",
                "0x1234" // Too short signature
        );
        assertFalse(result);
    }
}
