package com.xrpshield.blockchain;

import com.xrpshield.entity.BlockchainEventLogEntity;
import com.xrpshield.repository.BlockchainEventLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventListener {

    private static final Logger logger = LoggerFactory.getLogger(EventListener.class);

    private final BlockchainEventLogRepository eventLogRepository;

    public EventListener(BlockchainEventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }

    public void processEventLog(String eventName, String contractAddress, String txHash, Long blockNumber, Integer logIndex, String eventData) {
        logger.info("Processing event log {} from contract {} at block {}", eventName, contractAddress, blockNumber);
        BlockchainEventLogEntity entity = new BlockchainEventLogEntity(eventName, contractAddress, txHash, blockNumber, logIndex, eventData);
        eventLogRepository.save(entity);
    }

    public List<BlockchainEventLogEntity> getEventsByName(String eventName) {
        return eventLogRepository.findByEventName(eventName);
    }
}
