package com.xrpshield.repository;

import com.xrpshield.entity.PolicyHistoryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PolicyHistoryRepository extends BaseRepository<PolicyHistoryEntity> {

    List<PolicyHistoryEntity> findByPolicyIdOrderByPolicyVersionDesc(UUID policyId);
}
