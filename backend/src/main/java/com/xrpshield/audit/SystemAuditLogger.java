package com.xrpshield.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SystemAuditLogger {

    private static final Logger logger = LoggerFactory.getLogger(SystemAuditLogger.class);

    public void logAction(String actorAddress, String action, String resource, String details) {
        logger.info("AUDIT_EVENT | Actor: {} | Action: {} | Resource: {} | Details: {}",
                actorAddress != null ? actorAddress : "SYSTEM",
                action,
                resource,
                details != null ? details : "N/A");
    }
}
