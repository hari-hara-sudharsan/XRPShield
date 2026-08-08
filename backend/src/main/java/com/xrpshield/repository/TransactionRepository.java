package com.xrpshield.repository;

import com.xrpshield.entity.TransactionEntity;
import com.xrpshield.entity.TransactionStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends BaseRepository<TransactionEntity> {

    Optional<TransactionEntity> findByTxHash(String txHash);

    List<TransactionEntity> findByVaultId(UUID vaultId);

    List<TransactionEntity> findByStatus(TransactionStatus status);
}
