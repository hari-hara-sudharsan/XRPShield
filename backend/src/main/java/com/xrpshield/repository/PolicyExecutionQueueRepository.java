package com.xrpshield.repository;

import com.xrpshield.entity.PolicyExecutionQueueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PolicyExecutionQueueRepository extends JpaRepository<PolicyExecutionQueueEntity, UUID> {

    List<PolicyExecutionQueueEntity> findByStatus(String status);
}

