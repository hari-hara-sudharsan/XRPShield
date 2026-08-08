package com.xrpshield.repository;

import com.xrpshield.entity.DecisionMetadataEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DecisionMetadataRepository extends BaseRepository<DecisionMetadataEntity> {

    List<DecisionMetadataEntity> findByDecisionId(UUID decisionId);
}
