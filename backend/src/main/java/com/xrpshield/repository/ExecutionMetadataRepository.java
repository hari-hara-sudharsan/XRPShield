package com.xrpshield.repository;

import com.xrpshield.entity.ExecutionMetadataEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExecutionMetadataRepository extends BaseRepository<ExecutionMetadataEntity> {

    List<ExecutionMetadataEntity> findByExecutionId(UUID executionId);
}
