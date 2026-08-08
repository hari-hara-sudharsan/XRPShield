package com.xrpshield.repository;

import com.xrpshield.entity.DecisionHistoryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DecisionHistoryRepository extends BaseRepository<DecisionHistoryEntity> {

    List<DecisionHistoryEntity> findByDecisionIdOrderByDecisionVersionDesc(UUID decisionId);
}
