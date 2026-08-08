package com.xrpshield.mapper;

import com.xrpshield.dto.NotificationResponseDto;
import com.xrpshield.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper implements EntityMapper<NotificationEntity, NotificationResponseDto> {

    @Override
    public NotificationResponseDto toDto(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }
        return new NotificationResponseDto(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getTitle(),
                entity.getMessage(),
                entity.getSeverity() != null ? entity.getSeverity().name() : null,
                entity.isRead(),
                entity.getCreatedAt()
        );
    }

    @Override
    public NotificationEntity toEntity(NotificationResponseDto dto) {
        if (dto == null) {
            return null;
        }
        NotificationEntity entity = new NotificationEntity();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setMessage(dto.getMessage());
        entity.setRead(dto.isRead());
        return entity;
    }
}
