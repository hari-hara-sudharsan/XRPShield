package com.xrpshield.repository;

import com.xrpshield.entity.VaultEntity;
import com.xrpshield.entity.VaultStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VaultRepository extends BaseRepository<VaultEntity> {

    List<VaultEntity> findByOwnerId(UUID ownerId);

    Optional<VaultEntity> findByVaultAddress(String vaultAddress);

    List<VaultEntity> findByOwnerIdAndStatus(UUID ownerId, VaultStatus status);
}
