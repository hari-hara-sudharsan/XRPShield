package com.xrpshield.service;

import com.xrpshield.dto.AuthResponseDto;
import com.xrpshield.dto.LoginRequestDto;
import com.xrpshield.dto.RefreshTokenRequestDto;
import com.xrpshield.dto.RegisterRequestDto;
import com.xrpshield.dto.UserProfileResponseDto;
import com.xrpshield.dto.WalletResponseDto;
import com.xrpshield.entity.LoginHistoryEntity;
import com.xrpshield.entity.RefreshTokenEntity;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.entity.UserStatus;
import com.xrpshield.exception.BusinessException;
import com.xrpshield.exception.ResourceNotFoundException;
import com.xrpshield.mapper.WalletMapper;
import com.xrpshield.repository.LoginHistoryRepository;
import com.xrpshield.repository.RefreshTokenRepository;
import com.xrpshield.repository.UserRepository;
import com.xrpshield.security.JwtTokenProvider;
import com.xrpshield.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final WalletMapper walletMapper;

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, LoginHistoryRepository loginHistoryRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, WalletMapper walletMapper) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.walletMapper = walletMapper;
    }

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        logger.info("Processing user registration for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("User already exists with email: " + request.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        UserEntity user = new UserEntity(request.getEmail(), request.getDisplayName(), encodedPassword, Role.ROLE_USER, UserStatus.ACTIVE);
        UserEntity savedUser = userRepository.save(user);

        String accessToken = tokenProvider.generateAccessToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
        String refreshTokenStr = tokenProvider.generateRefreshToken(savedUser.getId());

        RefreshTokenEntity refreshToken = new RefreshTokenEntity(savedUser, refreshTokenStr, Instant.now().plusMillis(604800000), false);
        refreshTokenRepository.save(refreshToken);

        loginHistoryRepository.save(new LoginHistoryEntity(savedUser, "PASSWORD", savedUser.getEmail(), "SUCCESS", "127.0.0.1", "Browser", null));

        return new AuthResponseDto(accessToken, refreshTokenStr, savedUser.getId(), savedUser.getEmail(), savedUser.getDisplayName(), savedUser.getRole().name(), null);
    }

    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {
        logger.info("Processing login attempt for email: {}", request.getEmail());

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid email or password credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            loginHistoryRepository.save(new LoginHistoryEntity(user, "PASSWORD", request.getEmail(), "FAILED", "127.0.0.1", "Browser", "Invalid Password"));
            throw new BusinessException("Invalid email or password credentials");
        }

        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshTokenStr = tokenProvider.generateRefreshToken(user.getId());

        RefreshTokenEntity refreshToken = new RefreshTokenEntity(user, refreshTokenStr, Instant.now().plusMillis(604800000), false);
        refreshTokenRepository.save(refreshToken);

        loginHistoryRepository.save(new LoginHistoryEntity(user, "PASSWORD", user.getEmail(), "SUCCESS", "127.0.0.1", "Browser", null));

        return new AuthResponseDto(accessToken, refreshTokenStr, user.getId(), user.getEmail(), user.getDisplayName(), user.getRole().name(), null);
    }

    @Transactional
    public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {
        String tokenStr = request.getRefreshToken();
        if (!tokenProvider.validateToken(tokenStr)) {
            throw new BusinessException("Invalid or expired refresh token");
        }

        RefreshTokenEntity tokenEntity = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", "token", "provided_token"));

        if (tokenEntity.isRevoked() || tokenEntity.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("Refresh token has expired or been revoked");
        }

        UserEntity user = tokenEntity.getUser();
        String newAccessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());

        return new AuthResponseDto(newAccessToken, tokenStr, user.getId(), user.getEmail(), user.getDisplayName(), user.getRole().name(), null);
    }

    public UserProfileResponseDto getUserProfile(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<WalletResponseDto> walletDtos = user.getWallets().stream()
                .map(walletMapper::toDto)
                .collect(Collectors.toList());

        return new UserProfileResponseDto(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole().name(),
                user.getStatus().name(),
                walletDtos,
                user.getCreatedAt()
        );
    }
}
