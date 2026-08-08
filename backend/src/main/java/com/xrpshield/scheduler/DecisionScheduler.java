package com.xrpshield.scheduler;

import com.xrpshield.entity.DecisionQueueEntity;
import com.xrpshield.repository.DecisionQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class DecisionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DecisionScheduler.class);

    private final DecisionQueueRepository decisionQueueRepository;

    public DecisionScheduler(DecisionQueueRepository decisionQueueRepository) {
        this.decisionQueueRepository = decisionQueueRepository;
    }

    @Scheduled(fixedDelay = 30000)
    public void processDecisionQueue() {
        List<DecisionQueueEntity> queuedItems = decisionQueueRepository.findByStatus("QUEUED");
        if (queuedItems.isEmpty()) {
            return;
        }

        logger.info("Processing {} queued decision items in background...", queuedItems.size());
        for (DecisionQueueEntity item : queuedItems) {
            item.setStatus("COMPLETED");
            item.setProcessedAt(Instant.now());
            decisionQueueRepository.save(item);
        }
    }
}
