package com.xrpshield.repository;

import com.xrpshield.entity.NetworkConfigurationEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NetworkConfigurationRepository extends BaseRepository<NetworkConfigurationEntity> {

    Optional<NetworkConfigurationEntity> findByChainId(Long chainId);

    Optional<NetworkConfigurationEntity> findByNetworkName(String networkName);
}
