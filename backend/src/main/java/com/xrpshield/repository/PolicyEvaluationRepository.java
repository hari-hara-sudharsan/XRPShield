package com.xrpshield.repository;

import com.xrpshield.entity.PolicyEvaluationEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PolicyEvaluationRepository extends BaseRepository<PolicyEvaluationEntity> {

    List<PolicyEvaluationEntity> findByPolicyIdOrderByEvaluatedAtDesc(UUID policyId);

    Optional<PolicyEvaluationEntity> findTopByPolicyIdOrderByEvaluatedAtDesc(UUID policyId);
}
