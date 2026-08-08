package com.xrpshield.repository;

import com.xrpshield.entity.UserPreferenceEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPreferenceRepository extends BaseRepository<UserPreferenceEntity> {

    Optional<UserPreferenceEntity> findByUserId(UUID userId);
}
