package com.xrpshield.repository;

import com.xrpshield.entity.DeploymentHistoryEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeploymentHistoryRepository extends BaseRepository<DeploymentHistoryEntity> {

    Optional<DeploymentHistoryEntity> findByTxHash(String txHash);
}
