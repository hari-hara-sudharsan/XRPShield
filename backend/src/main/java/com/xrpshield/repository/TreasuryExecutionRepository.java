package com.xrpshield.repository;

import com.xrpshield.entity.TreasuryExecutionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TreasuryExecutionRepository extends BaseRepository<TreasuryExecutionEntity> {

    List<TreasuryExecutionEntity> findByVaultId(UUID vaultId);

    List<TreasuryExecutionEntity> findByExecutionState(String executionState);

    Optional<TreasuryExecutionEntity> findByDecisionId(UUID decisionId);

    Optional<TreasuryExecutionEntity> findByExecutionHash(String executionHash);
}
