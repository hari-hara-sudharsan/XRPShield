package com.xrpshield.repository;

import com.xrpshield.entity.ExecutionHistoryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExecutionHistoryRepository extends BaseRepository<ExecutionHistoryEntity> {

    List<ExecutionHistoryEntity> findByExecutionIdOrderByCreatedAtDesc(UUID executionId);
}
