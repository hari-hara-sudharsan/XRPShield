package com.xrpshield.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class EvaluateDecisionRequestDto {

    @NotNull(message = "Vault ID is required")
    private UUID vaultId;

    private UUID policyId;

    private String preferredDecisionType;

    public UUID getVaultId() {
        return vaultId;
    }

    public void setVaultId(UUID vaultId) {
        this.vaultId = vaultId;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public void setPolicyId(UUID policyId) {
        this.policyId = policyId;
    }

    public String getPreferredDecisionType() {
        return preferredDecisionType;
    }

    public void setPreferredDecisionType(String preferredDecisionType) {
        this.preferredDecisionType = preferredDecisionType;
    }
}
