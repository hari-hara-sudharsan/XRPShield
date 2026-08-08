package com.xrpshield.repository;

import com.xrpshield.entity.DecisionQueueEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecisionQueueRepository extends BaseRepository<DecisionQueueEntity> {

    List<DecisionQueueEntity> findByStatus(String status);
}
