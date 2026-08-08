package com.xrpshield.audit;

import com.xrpshield.entity.ConfidentialPolicyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PolicyAuditService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyAuditService.class);

    public void logPolicyEvent(ConfidentialPolicyEntity policy, String eventType, String actor, String details) {
        logger.info("POLICY_AUDIT_LOG | Policy: {} ({}) | Event: {} | Actor: {} | Details: {}",
                policy.getId(), policy.getPolicyName(), eventType, actor, details);
    }
}
