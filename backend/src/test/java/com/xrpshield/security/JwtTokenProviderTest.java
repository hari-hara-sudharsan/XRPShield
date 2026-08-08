package com.xrpshield.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
    }

    @Test
    @DisplayName("Should generate valid JWT access token with role claim")
    void testGenerateAndValidateAccessToken() {
        UUID userId = UUID.randomUUID();
        String identifier = "user@xrpshield.io";
        Role role = Role.ROLE_USER;

        String token = jwtTokenProvider.generateAccessToken(userId, identifier, role);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));

        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
        assertEquals("ROLE_USER", jwtTokenProvider.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Should reject malformed JWT token")
    void testInvalidToken() {
        assertFalse(jwtTokenProvider.validateToken("invalid.jwt.token"));
    }
}
