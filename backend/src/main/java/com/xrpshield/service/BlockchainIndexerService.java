package com.xrpshield.service;

import com.xrpshield.entity.BlockchainEventLogEntity;
import com.xrpshield.repository.BlockchainEventLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlockchainIndexerService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainIndexerService.class);
    private final BlockchainEventLogRepository eventLogRepository;

    public BlockchainIndexerService(BlockchainEventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }

    /**
     * Idempotently indexes a raw blockchain event log from Flare Coston2 Testnet
     */
    @Transactional
    public BlockchainEventLogEntity indexEvent(
            String txHash,
            Long blockNumber,
            Integer logIndex,
            String eventType,
            String walletAddress,
            String vaultId,
            String status,
            String rawPayload
    ) {
        if (eventLogRepository.existsByTransactionHashAndLogIndex(txHash, logIndex)) {
            logger.debug("Event already indexed for tx: {} logIndex: {}. Skipping ingestion.", txHash, logIndex);
            return null;
        }

        BlockchainEventLogEntity logEntity = new BlockchainEventLogEntity(
                txHash,
                blockNumber != null ? blockNumber : 33705000L,
                logIndex != null ? logIndex : 0,
                eventType,
                walletAddress,
                vaultId,
                LocalDateTime.now(),
                status != null ? status : "CONFIRMED",
                rawPayload
        );

        BlockchainEventLogEntity saved = eventLogRepository.save(logEntity);
        logger.info("Successfully indexed Flare event: {} for vault: {} in tx: {}", eventType, vaultId, txHash);
        return saved;
    }

    public List<BlockchainEventLogEntity> getEventsForWallet(String walletAddress) {
        if (walletAddress != null && !walletAddress.isBlank()) {
            return eventLogRepository.findByWalletAddressOrderByBlockNumberDesc(walletAddress);
        }
        return eventLogRepository.findTop50ByOrderByBlockNumberDesc();
    }

    public List<BlockchainEventLogEntity> getAllRecentEvents() {
        return eventLogRepository.findTop50ByOrderByBlockNumberDesc();
    }
}
