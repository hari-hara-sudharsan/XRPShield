package com.xrpshield.repository;

import com.xrpshield.entity.TreasuryDecisionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TreasuryDecisionRepository extends BaseRepository<TreasuryDecisionEntity> {

    List<TreasuryDecisionEntity> findByVaultId(UUID vaultId);

    List<TreasuryDecisionEntity> findByStatus(String status);

    Optional<TreasuryDecisionEntity> findByDecisionHash(String decisionHash);
}
