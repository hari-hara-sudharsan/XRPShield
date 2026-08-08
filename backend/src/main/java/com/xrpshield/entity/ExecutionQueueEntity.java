package com.xrpshield.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "execution_queue")
public class ExecutionQueueEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "execution_id", nullable = false)
    private TreasuryExecutionEntity execution;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries = 3;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "QUEUED"; // QUEUED, PROCESSING, COMPLETED, FAILED

    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt = Instant.now();

    @Column(name = "processed_at")
    private Instant processedAt;

    public ExecutionQueueEntity() {}

    public ExecutionQueueEntity(TreasuryExecutionEntity execution, Integer retryCount, Integer maxRetries, String status, Instant scheduledAt) {
        this.execution = execution;
        this.retryCount = retryCount != null ? retryCount : 0;
        this.maxRetries = maxRetries != null ? maxRetries : 3;
        this.status = status != null ? status : "QUEUED";
        this.scheduledAt = scheduledAt != null ? scheduledAt : Instant.now();
    }

    public TreasuryExecutionEntity getExecution() {
        return execution;
    }

    public void setExecution(TreasuryExecutionEntity execution) {
        this.execution = execution;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
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
