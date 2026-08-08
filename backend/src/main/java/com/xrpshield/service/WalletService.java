package com.xrpshield.service;

import com.xrpshield.dto.WalletRequestDto;
import com.xrpshield.dto.WalletResponseDto;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.entity.WalletEntity;
import com.xrpshield.exception.ResourceNotFoundException;
import com.xrpshield.mapper.WalletMapper;
import com.xrpshield.repository.UserRepository;
import com.xrpshield.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository, WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.walletMapper = walletMapper;
    }

    public WalletResponseDto createWallet(WalletRequestDto request) {
        logger.info("Registering wallet {} for user {}", request.getAddress(), request.getUserId());
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        WalletEntity wallet = new WalletEntity(user, request.getAddress(), request.getWalletType(), request.isPrimary());
        WalletEntity saved = walletRepository.save(wallet);
        return walletMapper.toDto(saved);
    }

    public WalletResponseDto getWalletById(UUID id) {
        WalletEntity wallet = walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "id", id));
        return walletMapper.toDto(wallet);
    }

    public List<WalletResponseDto> getWalletsByUserId(UUID userId) {
        return walletRepository.findByUserId(userId).stream()
                .map(walletMapper::toDto)
                .collect(Collectors.toList());
    }
}
