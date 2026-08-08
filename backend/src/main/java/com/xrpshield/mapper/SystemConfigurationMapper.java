package com.xrpshield.mapper;

import com.xrpshield.dto.SystemConfigurationDto;
import com.xrpshield.entity.SystemConfigurationEntity;
import org.springframework.stereotype.Component;

@Component
public class SystemConfigurationMapper implements EntityMapper<SystemConfigurationEntity, SystemConfigurationDto> {

    @Override
    public SystemConfigurationDto toDto(SystemConfigurationEntity entity) {
        if (entity == null) {
            return null;
        }
        return new SystemConfigurationDto(
                entity.getId(),
                entity.getConfigKey(),
                entity.getConfigValue(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Override
    public SystemConfigurationEntity toEntity(SystemConfigurationDto dto) {
        if (dto == null) {
            return null;
        }
        SystemConfigurationEntity entity = new SystemConfigurationEntity();
        entity.setId(dto.getId());
        entity.setConfigKey(dto.getConfigKey());
        entity.setConfigValue(dto.getConfigValue());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
