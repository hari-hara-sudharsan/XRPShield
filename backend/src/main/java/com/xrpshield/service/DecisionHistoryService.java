package com.xrpshield.service;

import com.xrpshield.entity.DecisionHistoryEntity;
import com.xrpshield.entity.TreasuryDecisionEntity;
import com.xrpshield.repository.DecisionHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DecisionHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(DecisionHistoryService.class);

    private final DecisionHistoryRepository decisionHistoryRepository;

    public DecisionHistoryService(DecisionHistoryRepository decisionHistoryRepository) {
        this.decisionHistoryRepository = decisionHistoryRepository;
    }

    public void recordHistory(TreasuryDecisionEntity decision, String action, String actorAddress, String details) {
        logger.info("Recording version {} history for decision {}", decision.getDecisionVersion(), decision.getId());
        DecisionHistoryEntity history = new DecisionHistoryEntity(
                decision, decision.getDecisionVersion(), action, actorAddress, details
        );
        decisionHistoryRepository.save(history);
    }

    public List<DecisionHistoryEntity> getHistory(UUID decisionId) {
        return decisionHistoryRepository.findByDecisionIdOrderByDecisionVersionDesc(decisionId);
    }
}
