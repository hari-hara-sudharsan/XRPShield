package com.xrpshield.repository;

import com.xrpshield.entity.NotificationEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends BaseRepository<NotificationEntity> {

    List<NotificationEntity> findByUserId(UUID userId);

    List<NotificationEntity> findByUserIdAndIsRead(UUID userId, boolean isRead);
}
