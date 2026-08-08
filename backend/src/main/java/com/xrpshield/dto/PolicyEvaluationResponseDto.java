package com.xrpshield.dto;

import java.time.Instant;
import java.util.UUID;

public class PolicyEvaluationResponseDto {

    private UUID id;
    private UUID policyId;
    private UUID vaultId;
    private String evaluationStatus;
    private String resultSummary;
    private Instant evaluatedAt;

    public PolicyEvaluationResponseDto() {}

    public PolicyEvaluationResponseDto(UUID id, UUID policyId, UUID vaultId, String evaluationStatus, String resultSummary, Instant evaluatedAt) {
        this.id = id;
        this.policyId = policyId;
        this.vaultId = vaultId;
        this.evaluationStatus = evaluationStatus;
        this.resultSummary = resultSummary;
        this.evaluatedAt = evaluatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public void setPolicyId(UUID policyId) {
        this.policyId = policyId;
    }

    public UUID getVaultId() {
        return vaultId;
    }

    public void setVaultId(UUID vaultId) {
        this.vaultId = vaultId;
    }

    public String getEvaluationStatus() {
        return evaluationStatus;
    }

    public void setEvaluationStatus(String evaluationStatus) {
        this.evaluationStatus = evaluationStatus;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }

    public Instant getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(Instant evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }
}
