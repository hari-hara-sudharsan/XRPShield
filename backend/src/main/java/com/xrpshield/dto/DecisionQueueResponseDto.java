package com.xrpshield.dto;

import java.time.Instant;
import java.util.UUID;

public class DecisionQueueResponseDto {

    private UUID id;
    private UUID decisionId;
    private String decisionType;
    private String vaultName;
    private String status;
    private Instant scheduledAt;
    private Instant processedAt;

    public DecisionQueueResponseDto() {}

    public DecisionQueueResponseDto(UUID id, UUID decisionId, String decisionType, String vaultName, String status, Instant scheduledAt, Instant processedAt) {
        this.id = id;
        this.decisionId = decisionId;
        this.decisionType = decisionType;
        this.vaultName = vaultName;
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

    public UUID getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(UUID decisionId) {
        this.decisionId = decisionId;
    }

    public String getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(String decisionType) {
        this.decisionType = decisionType;
    }

    public String getVaultName() {
        return vaultName;
    }

    public void setVaultName(String vaultName) {
        this.vaultName = vaultName;
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
