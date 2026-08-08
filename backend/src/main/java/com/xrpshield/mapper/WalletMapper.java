package com.xrpshield.mapper;

import com.xrpshield.dto.WalletResponseDto;
import com.xrpshield.entity.WalletEntity;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper implements EntityMapper<WalletEntity, WalletResponseDto> {

    @Override
    public WalletResponseDto toDto(WalletEntity entity) {
        if (entity == null) {
            return null;
        }
        return new WalletResponseDto(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getAddress(),
                entity.getWalletType() != null ? entity.getWalletType().name() : null,
                entity.isPrimary(),
                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getCreatedAt()
        );
    }

    @Override
    public WalletEntity toEntity(WalletResponseDto dto) {
        if (dto == null) {
            return null;
        }
        WalletEntity entity = new WalletEntity();
        entity.setId(dto.getId());
        entity.setAddress(dto.getAddress());
        entity.setPrimary(dto.isPrimary());
        return entity;
    }
}
