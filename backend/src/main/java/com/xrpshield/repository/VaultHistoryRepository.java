package com.xrpshield.repository;

import com.xrpshield.entity.VaultHistoryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VaultHistoryRepository extends BaseRepository<VaultHistoryEntity> {

    List<VaultHistoryEntity> findByVaultId(UUID vaultId);

    List<VaultHistoryEntity> findByVaultIdOrderByCreatedAtDesc(UUID vaultId);
}
