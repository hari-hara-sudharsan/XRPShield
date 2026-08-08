package com.xrpshield.repository;

import com.xrpshield.entity.ExecutionQueueEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExecutionQueueRepository extends BaseRepository<ExecutionQueueEntity> {

    List<ExecutionQueueEntity> findByStatus(String status);
}
