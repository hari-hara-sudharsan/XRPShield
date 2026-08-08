package com.xrpshield.repository;

import com.xrpshield.entity.VaultTransactionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VaultTransactionRepository extends BaseRepository<VaultTransactionEntity> {

    List<VaultTransactionEntity> findByVaultId(UUID vaultId);

    List<VaultTransactionEntity> findByVaultIdOrderByCreatedAtDesc(UUID vaultId);
}
