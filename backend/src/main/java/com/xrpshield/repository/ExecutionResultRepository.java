package com.xrpshield.repository;

import com.xrpshield.entity.ExecutionResultEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExecutionResultRepository extends BaseRepository<ExecutionResultEntity> {

    Optional<ExecutionResultEntity> findByExecutionId(UUID executionId);
}
