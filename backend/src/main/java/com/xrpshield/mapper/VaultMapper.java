package com.xrpshield.mapper;

import com.xrpshield.dto.VaultResponseDto;
import com.xrpshield.entity.VaultEntity;
import org.springframework.stereotype.Component;

@Component
public class VaultMapper implements EntityMapper<VaultEntity, VaultResponseDto> {

    @Override
    public VaultResponseDto toDto(VaultEntity entity) {
        if (entity == null) {
            return null;
        }
        return new VaultResponseDto(
                entity.getId(),
                entity.getOwner() != null ? entity.getOwner().getId() : null,
                entity.getVaultName(),
                entity.getVaultAddress(),
                entity.getAssetType(),
                entity.getBalance(),
                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getCreatedAt()
        );
    }

    @Override
    public VaultEntity toEntity(VaultResponseDto dto) {
        if (dto == null) {
            return null;
        }
        VaultEntity entity = new VaultEntity();
        entity.setId(dto.getId());
        entity.setVaultName(dto.getVaultName());
        entity.setVaultAddress(dto.getVaultAddress());
        entity.setAssetType(dto.getAssetType());
        entity.setBalance(dto.getBalance());
        return entity;
    }
}
