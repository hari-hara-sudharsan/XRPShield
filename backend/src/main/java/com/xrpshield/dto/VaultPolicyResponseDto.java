package com.xrpshield.dto;

import java.time.Instant;
import java.util.UUID;

public class VaultPolicyResponseDto {

    private UUID id;
    private UUID vaultId;
    private String policyName;
    private String description;
    private String confidentialHash;
    private String executionTrigger;
    private String status;
    private Instant createdAt;

    public VaultPolicyResponseDto() {}

    public VaultPolicyResponseDto(UUID id, UUID vaultId, String policyName, String description, String confidentialHash, String executionTrigger, String status, Instant createdAt) {
        this.id = id;
        this.vaultId = vaultId;
        this.policyName = policyName;
        this.description = description;
        this.confidentialHash = confidentialHash;
        this.executionTrigger = executionTrigger;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getVaultId() {
        return vaultId;
    }

    public void setVaultId(UUID vaultId) {
        this.vaultId = vaultId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfidentialHash() {
        return confidentialHash;
    }

    public void setConfidentialHash(String confidentialHash) {
        this.confidentialHash = confidentialHash;
    }

    public String getExecutionTrigger() {
        return executionTrigger;
    }

    public void setExecutionTrigger(String executionTrigger) {
        this.executionTrigger = executionTrigger;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
