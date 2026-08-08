package com.xrpshield.repository;

import com.xrpshield.entity.DecisionAuditEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DecisionAuditRepository extends BaseRepository<DecisionAuditEntity> {

    List<DecisionAuditEntity> findByDecisionId(UUID decisionId);
}
