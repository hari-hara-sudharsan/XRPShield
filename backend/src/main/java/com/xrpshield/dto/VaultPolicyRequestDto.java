package com.xrpshield.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class VaultPolicyRequestDto {

    @NotNull(message = "Vault ID is required")
    private UUID vaultId;

    @NotBlank(message = "Policy name is required")
    @Size(max = 100, message = "Policy name cannot exceed 100 characters")
    private String policyName;

    private String description;

    private String confidentialHash;

    @NotBlank(message = "Execution trigger is required")
    private String executionTrigger;

    public VaultPolicyRequestDto() {}

    public VaultPolicyRequestDto(UUID vaultId, String policyName, String description, String confidentialHash, String executionTrigger) {
        this.vaultId = vaultId;
        this.policyName = policyName;
        this.description = description;
        this.confidentialHash = confidentialHash;
        this.executionTrigger = executionTrigger;
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
}
