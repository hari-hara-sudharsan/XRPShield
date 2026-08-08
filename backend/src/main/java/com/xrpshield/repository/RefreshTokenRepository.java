package com.xrpshield.repository;

import com.xrpshield.entity.RefreshTokenEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends BaseRepository<RefreshTokenEntity> {

    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteByUserId(UUID userId);
}
