package com.xrpshield.repository;

import com.xrpshield.entity.ExecutionAuditEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExecutionAuditRepository extends BaseRepository<ExecutionAuditEntity> {

    List<ExecutionAuditEntity> findByExecutionId(UUID executionId);
}
