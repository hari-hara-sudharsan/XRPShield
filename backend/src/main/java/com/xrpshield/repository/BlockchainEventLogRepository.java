package com.xrpshield.repository;

import com.xrpshield.entity.BlockchainEventLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlockchainEventLogRepository extends JpaRepository<BlockchainEventLogEntity, UUID> {
    boolean existsByTransactionHashAndLogIndex(String transactionHash, Integer logIndex);
    List<BlockchainEventLogEntity> findByWalletAddressOrderByBlockNumberDesc(String walletAddress);
    List<BlockchainEventLogEntity> findByVaultIdOrderByBlockNumberDesc(String vaultId);
    List<BlockchainEventLogEntity> findTop50ByOrderByBlockNumberDesc();
}
