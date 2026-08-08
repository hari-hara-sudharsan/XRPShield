package com.xrpshield.mapper;

import com.xrpshield.dto.SessionResponseDto;
import com.xrpshield.entity.SessionEntity;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper implements EntityMapper<SessionEntity, SessionResponseDto> {

    @Override
    public SessionResponseDto toDto(SessionEntity entity) {
        if (entity == null) {
            return null;
        }
        return new SessionResponseDto(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getSessionToken(),
                entity.getNonce(),
                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getExpiresAt(),
                entity.getCreatedAt()
        );
    }

    @Override
    public SessionEntity toEntity(SessionResponseDto dto) {
        if (dto == null) {
            return null;
        }
        SessionEntity entity = new SessionEntity();
        entity.setId(dto.getId());
        entity.setSessionToken(dto.getSessionToken());
        entity.setNonce(dto.getNonce());
        entity.setExpiresAt(dto.getExpiresAt());
        return entity;
    }
}
