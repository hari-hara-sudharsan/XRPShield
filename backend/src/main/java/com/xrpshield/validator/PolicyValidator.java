package com.xrpshield.validator;

import com.xrpshield.dto.CreatePolicyRequestDto;
import com.xrpshield.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PolicyValidator {

    public void validatePolicyCreation(CreatePolicyRequestDto dto) {
        if (dto.getVaultId() == null) {
            throw new ValidationException("Vault ID must not be null");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ValidationException("Policy name must not be empty");
        }
        if (dto.getRiskThreshold() != null && (dto.getRiskThreshold().compareTo(BigDecimal.ZERO) < 0 || dto.getRiskThreshold().compareTo(BigDecimal.ONE) > 0)) {
            throw new ValidationException("Risk threshold must be between 0.0 and 1.0");
        }
    }
}
