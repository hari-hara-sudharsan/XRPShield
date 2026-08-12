package com.xrpshield.blockchain;

import com.xrpshield.entity.BlockchainEventLogEntity;
import com.xrpshield.repository.BlockchainEventLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventListener {

    private static final Logger logger = LoggerFactory.getLogger(EventListener.class);

    private final BlockchainEventLogRepository eventLogRepository;

    public EventListener(BlockchainEventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }

    public void processEventLog(String eventName, String contractAddress, String txHash, Long blockNumber, Integer logIndex, String eventData) {
        logger.info("Processing event log {} from contract {} at block {}", eventName, contractAddress, blockNumber);
        if (!eventLogRepository.existsByTransactionHashAndLogIndex(txHash, logIndex != null ? logIndex : 0)) {
            BlockchainEventLogEntity entity = new BlockchainEventLogEntity(
                txHash, blockNumber != null ? blockNumber : 33705000L, logIndex != null ? logIndex : 0,
                eventName, contractAddress, contractAddress, LocalDateTime.now(), "CONFIRMED", eventData
            );
            eventLogRepository.save(entity);
        }
    }

    public void recordDepositEvent(String userAddress, String amountWeiStr, String txHash, Long blockNumber) {
        logger.info("Indexing DepositExecuted event for user {} amount {} tx {}", userAddress, amountWeiStr, txHash);
        String eventData = String.format("{\"user\":\"%s\",\"amount\":\"%s\",\"type\":\"DEPOSIT\"}", userAddress, amountWeiStr);
        if (!eventLogRepository.existsByTransactionHashAndLogIndex(txHash, 0)) {
            BlockchainEventLogEntity entity = new BlockchainEventLogEntity(
                txHash, blockNumber != null ? blockNumber : 33705000L, 0,
                "FXRPDeposited", userAddress, userAddress, LocalDateTime.now(), "CONFIRMED", eventData
            );
            eventLogRepository.save(entity);
        }
    }

    public void recordWithdrawalEvent(String userAddress, String amountWeiStr, String txHash, Long blockNumber) {
        logger.info("Indexing WithdrawalExecuted event for user {} amount {} tx {}", userAddress, amountWeiStr, txHash);
        String eventData = String.format("{\"user\":\"%s\",\"amount\":\"%s\",\"type\":\"WITHDRAWAL\"}", userAddress, amountWeiStr);
        if (!eventLogRepository.existsByTransactionHashAndLogIndex(txHash, 0)) {
            BlockchainEventLogEntity entity = new BlockchainEventLogEntity(
                txHash, blockNumber != null ? blockNumber : 33705000L, 0,
                "FXRPWithdrawn", userAddress, userAddress, LocalDateTime.now(), "CONFIRMED", eventData
            );
            eventLogRepository.save(entity);
        }
    }

    public List<BlockchainEventLogEntity> getEventsByName(String eventName) {
        return eventLogRepository.findTop50ByOrderByBlockNumberDesc().stream()
                .filter(e -> eventName.equalsIgnoreCase(e.getEventType()))
                .collect(Collectors.toList());
    }
}
