package com.xrpshield.mapper;

import com.xrpshield.dto.PolicyResponseDto;
import com.xrpshield.entity.ConfidentialPolicyEntity;
import org.springframework.stereotype.Component;

@Component
public class PolicyMapper {

    public PolicyResponseDto toDto(ConfidentialPolicyEntity entity, String latestEvaluationStatus, String latestAttestationStatus) {
        if (entity == null) return null;
        return new PolicyResponseDto(
                entity.getId(),
                entity.getVault().getId(),
                entity.getVault().getVaultName(),
                entity.getPolicyName(),
                entity.getPolicyVersion(),
                entity.getStatus(),
                entity.getPublicMetadata(),
                entity.getPolicyHash(),
                latestEvaluationStatus != null ? latestEvaluationStatus : "COMPLIANT",
                latestAttestationStatus != null ? latestAttestationStatus : "VERIFIED",
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
