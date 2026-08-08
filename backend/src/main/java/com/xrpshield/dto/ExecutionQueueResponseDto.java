package com.xrpshield.dto;

import java.time.Instant;
import java.util.UUID;

public class ExecutionQueueResponseDto {

    private UUID id;
    private UUID executionId;
    private String vaultName;
    private Integer retryCount;
    private Integer maxRetries;
    private String status;
    private Instant scheduledAt;
    private Instant processedAt;

    public ExecutionQueueResponseDto() {}

    public ExecutionQueueResponseDto(UUID id, UUID executionId, String vaultName, Integer retryCount, Integer maxRetries, String status, Instant scheduledAt, Instant processedAt) {
        this.id = id;
        this.executionId = executionId;
        this.vaultName = vaultName;
        this.retryCount = retryCount;
        this.maxRetries = maxRetries;
        this.status = status;
        this.scheduledAt = scheduledAt;
        this.processedAt = processedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getExecutionId() {
        return executionId;
    }

    public void setExecutionId(UUID executionId) {
        this.executionId = executionId;
    }

    public String getVaultName() {
        return vaultName;
    }

    public void setVaultName(String vaultName) {
        this.vaultName = vaultName;
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
