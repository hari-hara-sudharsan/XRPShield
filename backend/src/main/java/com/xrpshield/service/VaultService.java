package com.xrpshield.service;

import com.xrpshield.audit.VaultAuditService;
import com.xrpshield.dto.*;
import com.xrpshield.entity.*;
import com.xrpshield.exception.BusinessException;
import com.xrpshield.exception.ResourceNotFoundException;
import com.xrpshield.mapper.VaultMapper;
import com.xrpshield.repository.*;
import com.xrpshield.validator.VaultValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VaultService {

    private static final Logger logger = LoggerFactory.getLogger(VaultService.class);

    private final VaultRepository vaultRepository;
    private final UserRepository userRepository;
    private final VaultBalanceRepository vaultBalanceRepository;
    private final VaultTransactionRepository vaultTransactionRepository;
    private final VaultHistoryRepository vaultHistoryRepository;
    private final VaultMapper vaultMapper;
    private final VaultValidator vaultValidator;
    private final VaultAuditService vaultAuditService;

    public VaultService(VaultRepository vaultRepository, UserRepository userRepository,
                        VaultBalanceRepository vaultBalanceRepository, VaultTransactionRepository vaultTransactionRepository,
                        VaultHistoryRepository vaultHistoryRepository, VaultMapper vaultMapper,
                        VaultValidator vaultValidator, VaultAuditService vaultAuditService) {
        this.vaultRepository = vaultRepository;
        this.userRepository = userRepository;
        this.vaultBalanceRepository = vaultBalanceRepository;
        this.vaultTransactionRepository = vaultTransactionRepository;
        this.vaultHistoryRepository = vaultHistoryRepository;
        this.vaultMapper = vaultMapper;
        this.vaultValidator = vaultValidator;
        this.vaultAuditService = vaultAuditService;
    }

    @Transactional
    public VaultDetailsDto createVault(CreateVaultRequestDto request, UserEntity user) {
        logger.info("Creating confidential vault {} for user {}", request.getName(), user.getEmail());

        String vaultAddress = request.getAddress();
        if (vaultAddress == null || vaultAddress.trim().isEmpty()) {
            vaultAddress = "0x" + UUID.randomUUID().toString().replace("-", "");
        }

        VaultEntity vault = new VaultEntity(
                user, request.getName(), vaultAddress,
                request.getAssetType() != null ? request.getAssetType() : "FXRP",
                BigDecimal.ZERO, VaultStatus.ACTIVE
        );

        VaultEntity savedVault = vaultRepository.save(vault);

        VaultBalanceEntity initialBalance = new VaultBalanceEntity(savedVault, "FXRP", BigDecimal.ZERO, BigDecimal.ZERO);
        vaultBalanceRepository.save(initialBalance);

        vaultHistoryRepository.save(new VaultHistoryEntity(savedVault, "VAULT_CREATED", user.getEmail(), "Created vault: " + request.getName()));
        vaultAuditService.logVaultActivity(savedVault, "VAULT_CREATED", user.getEmail(), "Created vault: " + request.getName());


        return getVaultDetails(savedVault.getId());

    }

    @Transactional
    public VaultTransactionResponseDto deposit(VaultDepositRequestDto request, UserEntity user) {
        vaultValidator.validatePositiveAmount(request.getAmount(), "depositAmount");

        VaultEntity vault = vaultRepository.findById(request.getVaultId())
                .orElseThrow(() -> new ResourceNotFoundException("Vault", "id", request.getVaultId()));

        VaultBalanceEntity balance = vaultBalanceRepository.findByVaultIdAndCurrency(vault.getId(), request.getCurrency())
                .orElseGet(() -> new VaultBalanceEntity(vault, request.getCurrency(), BigDecimal.ZERO, BigDecimal.ZERO));

        BigDecimal newBalance = balance.getBalanceAmount().add(request.getAmount());
        balance.setBalanceAmount(newBalance);
        vaultBalanceRepository.save(balance);

        vault.setBalance(newBalance);
        vaultRepository.save(vault);

        VaultTransactionEntity tx = new VaultTransactionEntity(
                vault, "DEPOSIT", request.getAmount(), request.getCurrency(),
                request.getTxHash() != null ? request.getTxHash() : "0x" + UUID.randomUUID().toString().replace("-", ""),
                request.getFromAddress() != null ? request.getFromAddress() : user.getEmail(),
                vault.getVaultAddress(), "CONFIRMED"
        );
        VaultTransactionEntity savedTx = vaultTransactionRepository.save(tx);

        vaultAuditService.logVaultActivity(vault, "DEPOSIT_COMPLETED", user.getEmail(),
                "Deposited " + request.getAmount() + " " + request.getCurrency());

        return new VaultTransactionResponseDto(
                savedTx.getId(), vault.getId(), savedTx.getTxType(), savedTx.getAmount(),
                savedTx.getCurrency(), savedTx.getTxHash(), savedTx.getFromAddress(),
                savedTx.getToAddress(), savedTx.getStatus(), savedTx.getCreatedAt()
        );
    }

    @Transactional
    public VaultTransactionResponseDto withdraw(VaultWithdrawalRequestDto request, UserEntity user) {
        vaultValidator.validatePositiveAmount(request.getAmount(), "withdrawalAmount");

        VaultEntity vault = vaultRepository.findById(request.getVaultId())
                .orElseThrow(() -> new ResourceNotFoundException("Vault", "id", request.getVaultId()));

        if (!vault.getOwner().getId().equals(user.getId())) {
            throw new BusinessException("Unauthorized: You do not own this vault");
        }

        VaultBalanceEntity balance = vaultBalanceRepository.findByVaultIdAndCurrency(vault.getId(), request.getCurrency())
                .orElseThrow(() -> new BusinessException("No balance record found for currency: " + request.getCurrency()));

        if (balance.getBalanceAmount().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Insufficient vault balance for withdrawal");
        }

        BigDecimal newBalance = balance.getBalanceAmount().subtract(request.getAmount());
        balance.setBalanceAmount(newBalance);
        vaultBalanceRepository.save(balance);

        vault.setBalance(newBalance);
        vaultRepository.save(vault);

        VaultTransactionEntity tx = new VaultTransactionEntity(
                vault, "WITHDRAWAL", request.getAmount(), request.getCurrency(),
                "0x" + UUID.randomUUID().toString().replace("-", ""),
                vault.getVaultAddress(),
                request.getRecipientAddress() != null ? request.getRecipientAddress() : user.getEmail(),
                "CONFIRMED"
        );
        VaultTransactionEntity savedTx = vaultTransactionRepository.save(tx);

        vaultAuditService.logVaultActivity(vault, "WITHDRAWAL_COMPLETED", user.getEmail(),
                "Withdrew " + request.getAmount() + " " + request.getCurrency());

        return new VaultTransactionResponseDto(
                savedTx.getId(), vault.getId(), savedTx.getTxType(), savedTx.getAmount(),
                savedTx.getCurrency(), savedTx.getTxHash(), savedTx.getFromAddress(),
                savedTx.getToAddress(), savedTx.getStatus(), savedTx.getCreatedAt()
        );
    }

    public VaultDetailsDto getVaultDetails(UUID id) {
        VaultEntity vault = vaultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vault", "id", id));

        VaultBalanceEntity balance = vaultBalanceRepository.findByVaultIdAndCurrency(vault.getId(), "FXRP")
                .orElse(null);

        BigDecimal balAmount = balance != null ? balance.getBalanceAmount() : vault.getBalance();
        BigDecimal lockedAmount = balance != null ? balance.getLockedAmount() : BigDecimal.ZERO;

        return new VaultDetailsDto(
                vault.getId(), vault.getVaultName(), "FXRP Confidential Vault",
                vault.getVaultAddress(), vault.getStatus().name(),
                vault.getOwner().getEmail(), balAmount, lockedAmount,
                vault.getAssetType(), vault.getCreatedAt()
        );
    }

    public List<VaultDetailsDto> getUserVaults(UUID userId) {
        return vaultRepository.findByOwnerId(userId).stream()
                .map(v -> getVaultDetails(v.getId()))
                .collect(Collectors.toList());
    }

    public List<VaultTransactionResponseDto> getVaultHistory(UUID vaultId) {
        return vaultTransactionRepository.findByVaultIdOrderByCreatedAtDesc(vaultId).stream()
                .map(tx -> new VaultTransactionResponseDto(
                        tx.getId(), tx.getVault().getId(), tx.getTxType(), tx.getAmount(),
                        tx.getCurrency(), tx.getTxHash(), tx.getFromAddress(),
                        tx.getToAddress(), tx.getStatus(), tx.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public VaultResponseDto createVault(VaultRequestDto request) {
        UserEntity owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getOwnerId()));

        VaultEntity vault = new VaultEntity(owner, request.getVaultName(), request.getVaultAddress(), request.getAssetType(), request.getInitialBalance(), VaultStatus.ACTIVE);
        VaultEntity saved = vaultRepository.save(vault);
        return vaultMapper.toDto(saved);
    }

    public VaultResponseDto getVaultById(UUID id) {
        VaultEntity vault = vaultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vault", "id", id));
        return vaultMapper.toDto(vault);
    }

    public List<VaultResponseDto> getVaultsByOwner(UUID ownerId) {
        return vaultRepository.findByOwnerId(ownerId).stream()
                .map(vaultMapper::toDto)
                .collect(Collectors.toList());
    }
}
