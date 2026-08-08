package com.xrpshield.repository;

import com.xrpshield.entity.AuditLogEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends BaseRepository<AuditLogEntity> {

    List<AuditLogEntity> findByUserId(UUID userId);

    List<AuditLogEntity> findByAction(String action);
}
