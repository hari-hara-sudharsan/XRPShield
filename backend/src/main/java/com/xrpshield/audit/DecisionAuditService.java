package com.xrpshield.audit;

import com.xrpshield.entity.DecisionAuditEntity;
import com.xrpshield.entity.TreasuryDecisionEntity;
import com.xrpshield.repository.DecisionAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DecisionAuditService {

    private static final Logger logger = LoggerFactory.getLogger(DecisionAuditService.class);

    private final DecisionAuditRepository decisionAuditRepository;

    public DecisionAuditService(DecisionAuditRepository decisionAuditRepository) {
        this.decisionAuditRepository = decisionAuditRepository;
    }

    public void logDecisionEvent(TreasuryDecisionEntity decision, String eventType, String actor, String details) {
        logger.info("DECISION_AUDIT_LOG | Decision: {} ({}) | Event: {} | Actor: {} | Details: {}",
                decision.getId(), decision.getDecisionType(), eventType, actor, details);

        DecisionAuditEntity audit = new DecisionAuditEntity(decision, eventType, actor, details);
        decisionAuditRepository.save(audit);
    }
}
