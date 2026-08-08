package com.xrpshield.repository;

import com.xrpshield.entity.VaultBalanceEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VaultBalanceRepository extends BaseRepository<VaultBalanceEntity> {

    List<VaultBalanceEntity> findByVaultId(UUID vaultId);

    Optional<VaultBalanceEntity> findByVaultIdAndCurrency(UUID vaultId, String currency);
}
