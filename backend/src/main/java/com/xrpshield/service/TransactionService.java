package com.xrpshield.service;

import com.xrpshield.dto.TransactionRequestDto;
import com.xrpshield.dto.TransactionResponseDto;
import com.xrpshield.entity.TransactionEntity;
import com.xrpshield.entity.TransactionStatus;
import com.xrpshield.entity.VaultEntity;
import com.xrpshield.exception.ResourceNotFoundException;
import com.xrpshield.mapper.TransactionMapper;
import com.xrpshield.repository.TransactionRepository;
import com.xrpshield.repository.VaultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final VaultRepository vaultRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository, VaultRepository vaultRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.vaultRepository = vaultRepository;
        this.transactionMapper = transactionMapper;
    }

    public TransactionResponseDto recordTransaction(TransactionRequestDto request) {
        logger.info("Recording transaction of type {} for amount {}", request.getTransactionType(), request.getAmount());
        VaultEntity vault = null;
        if (request.getVaultId() != null) {
            vault = vaultRepository.findById(request.getVaultId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vault", "id", request.getVaultId()));
        }

        TransactionEntity tx = new TransactionEntity(vault, request.getTxHash(), request.getTransactionType(), request.getAmount(), request.getAsset(), TransactionStatus.PENDING, request.getAttestationProof(), request.getBlockNumber());
        TransactionEntity saved = transactionRepository.save(tx);
        return transactionMapper.toDto(saved);
    }

    public TransactionResponseDto getTransactionById(UUID id) {
        TransactionEntity tx = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
        return transactionMapper.toDto(tx);
    }

    public List<TransactionResponseDto> getTransactionsByVaultId(UUID vaultId) {
        return transactionRepository.findByVaultId(vaultId).stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }
}
