package com.xrpshield.mapper;

import com.xrpshield.dto.VaultPolicyResponseDto;
import com.xrpshield.entity.VaultPolicyEntity;
import org.springframework.stereotype.Component;

@Component
public class VaultPolicyMapper implements EntityMapper<VaultPolicyEntity, VaultPolicyResponseDto> {

    @Override
    public VaultPolicyResponseDto toDto(VaultPolicyEntity entity) {
        if (entity == null) {
            return null;
        }
        return new VaultPolicyResponseDto(
                entity.getId(),
                entity.getVault() != null ? entity.getVault().getId() : null,
                entity.getPolicyName(),
                entity.getDescription(),
                entity.getConfidentialHash(),
                entity.getExecutionTrigger(),
                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getCreatedAt()
        );
    }

    @Override
    public VaultPolicyEntity toEntity(VaultPolicyResponseDto dto) {
        if (dto == null) {
            return null;
        }
        VaultPolicyEntity entity = new VaultPolicyEntity();
        entity.setId(dto.getId());
        entity.setPolicyName(dto.getPolicyName());
        entity.setDescription(dto.getDescription());
        entity.setConfidentialHash(dto.getConfidentialHash());
        entity.setExecutionTrigger(dto.getExecutionTrigger());
        return entity;
    }
}
