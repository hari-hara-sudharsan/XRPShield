package com.xrpshield.service;

import com.xrpshield.dto.AuthResponseDto;
import com.xrpshield.dto.RegisterRequestDto;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.mapper.WalletMapper;
import com.xrpshield.repository.LoginHistoryRepository;
import com.xrpshield.repository.RefreshTokenRepository;
import com.xrpshield.repository.UserRepository;
import com.xrpshield.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private LoginHistoryRepository loginHistoryRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private WalletMapper walletMapper;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        refreshTokenRepository = Mockito.mock(RefreshTokenRepository.class);
        loginHistoryRepository = Mockito.mock(LoginHistoryRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        jwtTokenProvider = new JwtTokenProvider();
        walletMapper = new WalletMapper();

        authService = new AuthService(userRepository, refreshTokenRepository, loginHistoryRepository, passwordEncoder, jwtTokenProvider, walletMapper);
    }

    @Test
    @DisplayName("Should successfully register user and return JWT auth tokens")
    void testUserRegistration() {
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("test@xrpshield.io");
        dto.setPassword("SecretPassword123");
        dto.setDisplayName("Test User");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });

        AuthResponseDto response = authService.register(dto);

        assertNotNull(response);
        assertEquals("test@xrpshield.io", response.getEmail());
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }
}
