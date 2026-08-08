package com.xrpshield.repository;

import com.xrpshield.entity.DecisionEvaluationEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DecisionEvaluationRepository extends BaseRepository<DecisionEvaluationEntity> {

    List<DecisionEvaluationEntity> findByDecisionIdOrderByEvaluatedAtDesc(UUID decisionId);

    Optional<DecisionEvaluationEntity> findTopByDecisionIdOrderByEvaluatedAtDesc(UUID decisionId);
}
