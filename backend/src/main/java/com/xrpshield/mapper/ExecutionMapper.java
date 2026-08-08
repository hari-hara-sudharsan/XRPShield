package com.xrpshield.mapper;

import com.xrpshield.dto.ExecutionResponseDto;
import com.xrpshield.entity.TreasuryExecutionEntity;
import org.springframework.stereotype.Component;

@Component
public class ExecutionMapper {

    public ExecutionResponseDto toDto(TreasuryExecutionEntity entity) {
        if (entity == null) return null;
        return new ExecutionResponseDto(
                entity.getId(),
                entity.getDecision().getId(),
                entity.getDecision().getDecisionType(),
                entity.getVault().getId(),
                entity.getVault().getVaultName(),
                entity.getExecutionState(),
                entity.getTxHash(),
                entity.getBlockNumber(),
                entity.getGasUsed(),
                entity.getExecutionHash(),
                entity.getCreatedAt(),
                entity.getCompletedAt()
        );
    }
}
