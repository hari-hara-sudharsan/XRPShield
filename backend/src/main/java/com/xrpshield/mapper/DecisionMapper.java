package com.xrpshield.mapper;

import com.xrpshield.dto.DecisionResponseDto;
import com.xrpshield.entity.TreasuryDecisionEntity;
import org.springframework.stereotype.Component;

@Component
public class DecisionMapper {

    public DecisionResponseDto toDto(TreasuryDecisionEntity entity) {
        if (entity == null) return null;
        return new DecisionResponseDto(
                entity.getId(),
                entity.getVault().getId(),
                entity.getVault().getVaultName(),
                entity.getPolicy() != null ? entity.getPolicy().getId() : null,
                entity.getPolicy() != null ? entity.getPolicy().getPolicyName() : "Default Treasury Policy",
                entity.getDecisionType(),
                entity.getDecisionVersion(),
                entity.getStatus(),
                entity.getRationale(),
                entity.getAttestationId(),
                entity.getDecisionHash(),
                entity.getCreatedAt()
        );
    }
}
