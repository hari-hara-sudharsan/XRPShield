package com.xrpshield.repository;

import com.xrpshield.entity.SessionEntity;
import com.xrpshield.entity.SessionStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends BaseRepository<SessionEntity> {

    Optional<SessionEntity> findBySessionToken(String sessionToken);

    List<SessionEntity> findByUserIdAndStatus(UUID userId, SessionStatus status);
}
