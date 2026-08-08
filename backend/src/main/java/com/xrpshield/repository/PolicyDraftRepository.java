package com.xrpshield.repository;

import com.xrpshield.entity.PolicyDraftEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PolicyDraftRepository extends BaseRepository<PolicyDraftEntity> {

    List<PolicyDraftEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
