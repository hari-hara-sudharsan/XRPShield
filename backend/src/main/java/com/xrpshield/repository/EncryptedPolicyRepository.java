package com.xrpshield.repository;

import com.xrpshield.entity.EncryptedPolicyEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EncryptedPolicyRepository extends BaseRepository<EncryptedPolicyEntity> {

    Optional<EncryptedPolicyEntity> findByPolicyId(UUID policyId);
}
