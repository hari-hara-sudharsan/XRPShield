package com.xrpshield.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "decision_queue")
public class DecisionQueueEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "decision_id", nullable = false)
    private TreasuryDecisionEntity decision;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "QUEUED"; // QUEUED, PROCESSING, COMPLETED, FAILED

    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt = Instant.now();

    @Column(name = "processed_at")
    private Instant processedAt;

    public DecisionQueueEntity() {}

    public DecisionQueueEntity(TreasuryDecisionEntity decision, String status, Instant scheduledAt) {
        this.decision = decision;
        this.status = status != null ? status : "QUEUED";
        this.scheduledAt = scheduledAt != null ? scheduledAt : Instant.now();
    }

    public TreasuryDecisionEntity getDecision() {
        return decision;
    }

    public void setDecision(TreasuryDecisionEntity decision) {
        this.decision = decision;
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

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
}
