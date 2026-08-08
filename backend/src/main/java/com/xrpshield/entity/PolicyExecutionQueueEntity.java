package com.xrpshield.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "policy_execution_queue")
public class PolicyExecutionQueueEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private ConfidentialPolicyEntity policy;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, SUBMITTED, COMPLETED, FAILED

    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt = Instant.now();

    @Column(name = "executed_at")
    private Instant executedAt;

    public PolicyExecutionQueueEntity() {}

    public PolicyExecutionQueueEntity(ConfidentialPolicyEntity policy, String status, Instant scheduledAt) {
        this.policy = policy;
        this.status = status != null ? status : "PENDING";
        this.scheduledAt = scheduledAt != null ? scheduledAt : Instant.now();
    }

    public ConfidentialPolicyEntity getPolicy() {
        return policy;
    }

    public void setPolicy(ConfidentialPolicyEntity policy) {
        this.policy = policy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Instant getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Instant executedAt) {
        this.executedAt = executedAt;
    }
}
