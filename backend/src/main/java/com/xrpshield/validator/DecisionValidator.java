package com.xrpshield.validator;

import com.xrpshield.dto.EvaluateDecisionRequestDto;
import com.xrpshield.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DecisionValidator {

    private static final Set<String> ALLOWED_DECISIONS = Set.of(
            "NO_ACTION", "PROTECT_POSITION", "REDUCE_EXPOSURE",
            "INCREASE_PROTECTION", "REQUEST_REVIEW", "EMERGENCY_EXIT"
    );

    public void validateEvaluationRequest(EvaluateDecisionRequestDto dto) {
        if (dto.getVaultId() == null) {
            throw new ValidationException("Vault ID is required for decision evaluation");
        }
        if (dto.getPreferredDecisionType() != null && !ALLOWED_DECISIONS.contains(dto.getPreferredDecisionType())) {
            throw new ValidationException("Invalid decision type: " + dto.getPreferredDecisionType());
        }
    }
}
