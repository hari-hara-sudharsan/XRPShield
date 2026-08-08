package com.xrpshield.service;

import com.xrpshield.dto.AuthResponseDto;
import com.xrpshield.dto.WalletNonceResponseDto;
import com.xrpshield.dto.WalletVerifyRequestDto;
import com.xrpshield.entity.LoginHistoryEntity;
import com.xrpshield.entity.RefreshTokenEntity;
import com.xrpshield.entity.SessionEntity;
import com.xrpshield.entity.SessionStatus;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.entity.UserStatus;
import com.xrpshield.entity.WalletEntity;
import com.xrpshield.entity.WalletType;
import com.xrpshield.exception.BusinessException;
import com.xrpshield.repository.LoginHistoryRepository;
import com.xrpshield.repository.RefreshTokenRepository;
import com.xrpshield.repository.SessionRepository;
import com.xrpshield.repository.UserRepository;
import com.xrpshield.repository.WalletRepository;
import com.xrpshield.security.JwtTokenProvider;
import com.xrpshield.security.Role;
import com.xrpshield.security.Web3SignatureVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class WalletAuthService {

    private static final Logger logger = LoggerFactory.getLogger(WalletAuthService.class);
    private static final String NONCE_MESSAGE_TEMPLATE = "Sign in to XRPShield Platform.\nNonce: %s\nTimestamp: %s";

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final SessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final Web3SignatureVerifier signatureVerifier;
    private final JwtTokenProvider tokenProvider;

    public WalletAuthService(UserRepository userRepository, WalletRepository walletRepository, SessionRepository sessionRepository, RefreshTokenRepository refreshTokenRepository, LoginHistoryRepository loginHistoryRepository, Web3SignatureVerifier signatureVerifier, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.sessionRepository = sessionRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.signatureVerifier = signatureVerifier;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public WalletNonceResponseDto generateNonce(String address) {
        logger.info("Generating Web3 authentication nonce for wallet: {}", address);

        String nonce = UUID.randomUUID().toString();
        String messageToSign = String.format(NONCE_MESSAGE_TEMPLATE, nonce, Instant.now().toString());

        UserEntity user = walletRepository.findByAddress(address)
                .map(WalletEntity::getUser)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity(null, "Wallet User (" + address.substring(0, 6) + "...) ", null, Role.ROLE_USER, UserStatus.ACTIVE);
                    return userRepository.save(newUser);
                });

        SessionEntity session = new SessionEntity(user, UUID.randomUUID().toString(), nonce, SessionStatus.ACTIVE, Instant.now().plusSeconds(600));
        sessionRepository.save(session);

        return new WalletNonceResponseDto(address, nonce, messageToSign);
    }

    @Transactional
    public AuthResponseDto verifySignatureAndLogin(WalletVerifyRequestDto request) {
        logger.info("Verifying Web3 wallet signature for address: {}", request.getAddress());

        boolean isValid = false;
        if (request.getMessageToSign() != null && !request.getMessageToSign().trim().isEmpty()) {
            isValid = signatureVerifier.verifySignature(request.getAddress(), request.getMessageToSign(), request.getSignature());
        }
        if (!isValid) {
            isValid = signatureVerifier.verifySignature(request.getAddress(), request.getNonce(), request.getSignature());
        }

        if (!isValid) {
            loginHistoryRepository.save(new LoginHistoryEntity(null, "WALLET", request.getAddress(), "FAILED", "127.0.0.1", "MetaMask", "Signature Verification Failed"));
            throw new BusinessException("Cryptographic wallet signature verification failed");
        }


        WalletEntity wallet = walletRepository.findByAddress(request.getAddress())
                .orElseGet(() -> {
                    UserEntity user = new UserEntity(null, "User " + request.getAddress().substring(0, 6), null, Role.ROLE_USER, UserStatus.ACTIVE);
                    UserEntity savedUser = userRepository.save(user);
                    WalletEntity newWallet = new WalletEntity(savedUser, request.getAddress(), WalletType.EVM, true);
                    return walletRepository.save(newWallet);
                });

        UserEntity user = wallet.getUser();
        String accessToken = tokenProvider.generateAccessToken(user.getId(), wallet.getAddress(), user.getRole());
        String refreshTokenStr = tokenProvider.generateRefreshToken(user.getId());

        RefreshTokenEntity refreshToken = new RefreshTokenEntity(user, refreshTokenStr, Instant.now().plusMillis(604800000), false);
        refreshTokenRepository.save(refreshToken);

        loginHistoryRepository.save(new LoginHistoryEntity(user, "WALLET", request.getAddress(), "SUCCESS", "127.0.0.1", "MetaMask", null));

        return new AuthResponseDto(accessToken, refreshTokenStr, user.getId(), user.getEmail(), user.getDisplayName(), user.getRole().name(), wallet.getAddress());
    }
}
