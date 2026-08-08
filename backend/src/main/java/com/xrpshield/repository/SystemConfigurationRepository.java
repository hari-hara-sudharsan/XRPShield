package com.xrpshield.repository;

import com.xrpshield.entity.SystemConfigurationEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemConfigurationRepository extends BaseRepository<SystemConfigurationEntity> {

    Optional<SystemConfigurationEntity> findByConfigKey(String configKey);

    boolean existsByConfigKey(String configKey);
}
