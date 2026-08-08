package com.xrpshield.repository;

import com.xrpshield.entity.LoginHistoryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoginHistoryRepository extends BaseRepository<LoginHistoryEntity> {

    List<LoginHistoryEntity> findByUserId(UUID userId);
}
