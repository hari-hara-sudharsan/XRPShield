package com.xrpshield.mapper;

import com.xrpshield.dto.UserRequestDto;
import com.xrpshield.dto.UserResponseDto;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements EntityMapper<UserEntity, UserResponseDto> {

    @Override
    public UserResponseDto toDto(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return new UserResponseDto(
                entity.getId(),
                entity.getEmail(),
                entity.getDisplayName(),
                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Override
    public UserEntity toEntity(UserResponseDto dto) {
        if (dto == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(dto.getId());
        entity.setEmail(dto.getEmail());
        entity.setDisplayName(dto.getDisplayName());
        if (dto.getStatus() != null) {
            entity.setStatus(UserStatus.valueOf(dto.getStatus()));
        }
        return entity;
    }

    public UserEntity toEntityFromRequest(UserRequestDto request) {
        if (request == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setEmail(request.getEmail());
        entity.setDisplayName(request.getDisplayName());
        entity.setStatus(UserStatus.ACTIVE);
        return entity;
    }
}
