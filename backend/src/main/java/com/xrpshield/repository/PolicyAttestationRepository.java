package com.xrpshield.repository;

import com.xrpshield.entity.PolicyAttestationEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PolicyAttestationRepository extends BaseRepository<PolicyAttestationEntity> {

    List<PolicyAttestationEntity> findByPolicyIdOrderByAttestedAtDesc(UUID policyId);

    Optional<PolicyAttestationEntity> findTopByPolicyIdOrderByAttestedAtDesc(UUID policyId);
}
