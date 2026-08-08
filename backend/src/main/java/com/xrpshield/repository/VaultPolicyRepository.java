package com.xrpshield.repository;

import com.xrpshield.entity.PolicyStatus;
import com.xrpshield.entity.VaultPolicyEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VaultPolicyRepository extends BaseRepository<VaultPolicyEntity> {

    List<VaultPolicyEntity> findByVaultId(UUID vaultId);

    List<VaultPolicyEntity> findByVaultIdAndStatus(UUID vaultId, PolicyStatus status);
}
