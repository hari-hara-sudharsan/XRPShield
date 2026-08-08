package com.xrpshield.repository;

import com.xrpshield.entity.BlockchainTransactionLogEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockchainTransactionLogRepository extends BaseRepository<BlockchainTransactionLogEntity> {

    Optional<BlockchainTransactionLogEntity> findByTxHash(String txHash);
}
