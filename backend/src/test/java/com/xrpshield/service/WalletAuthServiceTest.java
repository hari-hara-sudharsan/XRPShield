package com.xrpshield.service;

import com.xrpshield.dto.WalletNonceResponseDto;
import com.xrpshield.repository.*;
import com.xrpshield.security.JwtTokenProvider;
import com.xrpshield.security.Web3SignatureVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class WalletAuthServiceTest {

    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private SessionRepository sessionRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private LoginHistoryRepository loginHistoryRepository;
    private Web3SignatureVerifier signatureVerifier;
    private JwtTokenProvider jwtTokenProvider;
    private WalletAuthService walletAuthService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        walletRepository = Mockito.mock(WalletRepository.class);
        sessionRepository = Mockito.mock(SessionRepository.class);
        refreshTokenRepository = Mockito.mock(RefreshTokenRepository.class);
        loginHistoryRepository = Mockito.mock(LoginHistoryRepository.class);
        signatureVerifier = new Web3SignatureVerifier();
        jwtTokenProvider = new JwtTokenProvider();

        walletAuthService = new WalletAuthService(
                userRepository, walletRepository, sessionRepository,
                refreshTokenRepository, loginHistoryRepository, signatureVerifier, jwtTokenProvider
        );
    }

    @Test
    @DisplayName("Should generate valid nonces for wallet authentication")
    void testGenerateNonce() {
        String address = "0x1111111111111111111111111111111111111111";

        WalletNonceResponseDto response = walletAuthService.generateNonce(address);

        assertNotNull(response);
        assertEquals(address, response.getAddress());
        assertNotNull(response.getNonce());
        assertTrue(response.getMessageToSign().contains(response.getNonce()));
    }
}
