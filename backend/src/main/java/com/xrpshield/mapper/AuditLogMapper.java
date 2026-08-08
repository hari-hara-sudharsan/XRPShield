package com.xrpshield.mapper;

import com.xrpshield.dto.AuditLogResponseDto;
import com.xrpshield.entity.AuditLogEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper implements EntityMapper<AuditLogEntity, AuditLogResponseDto> {

    @Override
    public AuditLogResponseDto toDto(AuditLogEntity entity) {
        if (entity == null) {
            return null;
        }
        return new AuditLogResponseDto(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getAction(),
                entity.getResource(),
                entity.getDetails(),
                entity.getIpAddress(),
                entity.getCreatedAt()
        );
    }

    @Override
    public AuditLogEntity toEntity(AuditLogResponseDto dto) {
        if (dto == null) {
            return null;
        }
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(dto.getId());
        entity.setAction(dto.getAction());
        entity.setResource(dto.getResource());
        entity.setDetails(dto.getDetails());
        entity.setIpAddress(dto.getIpAddress());
        return entity;
    }
}
