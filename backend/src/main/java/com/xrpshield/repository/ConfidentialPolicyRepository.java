package com.xrpshield.repository;

import com.xrpshield.entity.ConfidentialPolicyEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfidentialPolicyRepository extends BaseRepository<ConfidentialPolicyEntity> {

    List<ConfidentialPolicyEntity> findByVaultId(UUID vaultId);

    Optional<ConfidentialPolicyEntity> findByPolicyHash(String policyHash);
}
