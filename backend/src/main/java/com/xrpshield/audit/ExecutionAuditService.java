package com.xrpshield.audit;

import com.xrpshield.entity.ExecutionAuditEntity;
import com.xrpshield.entity.TreasuryExecutionEntity;
import com.xrpshield.repository.ExecutionAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExecutionAuditService {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionAuditService.class);

    private final ExecutionAuditRepository executionAuditRepository;

    public ExecutionAuditService(ExecutionAuditRepository executionAuditRepository) {
        this.executionAuditRepository = executionAuditRepository;
    }

    public void logExecutionEvent(TreasuryExecutionEntity execution, String eventType, String actor, String txHash, String wallet, String details) {
        logger.info("EXECUTION_AUDIT_LOG | Exec: {} | State: {} | Event: {} | Actor: {} | Tx: {}",
                execution.getId(), execution.getExecutionState(), eventType, actor, txHash);

        ExecutionAuditEntity audit = new ExecutionAuditEntity(
                execution, eventType, actor, txHash, wallet, details
        );
        executionAuditRepository.save(audit);
    }
}
