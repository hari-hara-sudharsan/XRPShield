package com.xrpshield.repository;

import com.xrpshield.entity.BlockchainEventLogEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockchainEventLogRepository extends BaseRepository<BlockchainEventLogEntity> {

    List<BlockchainEventLogEntity> findByEventName(String eventName);

    List<BlockchainEventLogEntity> findByContractAddress(String contractAddress);
}
