package com.xrpshield.repository;

import com.xrpshield.entity.FeatureFlagEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeatureFlagRepository extends BaseRepository<FeatureFlagEntity> {

    Optional<FeatureFlagEntity> findByFlagKey(String flagKey);
}
