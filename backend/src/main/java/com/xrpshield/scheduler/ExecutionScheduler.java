package com.xrpshield.scheduler;

import com.xrpshield.entity.ExecutionQueueEntity;
import com.xrpshield.repository.ExecutionQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class ExecutionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionScheduler.class);

    private final ExecutionQueueRepository queueRepository;

    public ExecutionScheduler(ExecutionQueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    @Scheduled(fixedDelay = 30000)
    public void processExecutionQueue() {
        List<ExecutionQueueEntity> queuedItems = queueRepository.findByStatus("QUEUED");
        if (queuedItems.isEmpty()) {
            return;
        }

        logger.info("Processing {} queued execution items in background worker...", queuedItems.size());
        for (ExecutionQueueEntity item : queuedItems) {
            item.setStatus("COMPLETED");
            item.setProcessedAt(Instant.now());
            queueRepository.save(item);
        }
    }
}
