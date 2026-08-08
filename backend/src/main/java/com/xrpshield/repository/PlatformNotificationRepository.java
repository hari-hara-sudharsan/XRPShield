package com.xrpshield.repository;

import com.xrpshield.entity.PlatformNotificationEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlatformNotificationRepository extends BaseRepository<PlatformNotificationEntity> {

    List<PlatformNotificationEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<PlatformNotificationEntity> findBySeverityOrderByCreatedAtDesc(String severity);
}
