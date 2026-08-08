package com.xrpshield.validator;

import com.xrpshield.dto.StartExecutionRequestDto;
import com.xrpshield.entity.TreasuryDecisionEntity;
import com.xrpshield.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class ExecutionValidator {

    public void validateStartRequest(StartExecutionRequestDto dto) {
        if (dto.getDecisionId() == null) {
            throw new ValidationException("Decision ID is required to start execution");
        }
    }

    public void validateDecisionEligibility(TreasuryDecisionEntity decision) {
        if (decision == null) {
            throw new ValidationException("Target treasury decision does not exist");
        }
        if (!"APPROVED".equalsIgnoreCase(decision.getStatus()) && !"PENDING".equalsIgnoreCase(decision.getStatus())) {
            throw new ValidationException("Decision status must be APPROVED or PENDING for execution");
        }
    }
}
