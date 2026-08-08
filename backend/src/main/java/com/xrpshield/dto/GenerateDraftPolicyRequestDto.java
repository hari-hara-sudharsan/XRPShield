package com.xrpshield.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class GenerateDraftPolicyRequestDto {

    private UUID vaultId;

    @NotBlank(message = "Policy intent description is required")
    private String intent;

    public UUID getVaultId() {
        return vaultId;
    }

    public void setVaultId(UUID vaultId) {
        this.vaultId = vaultId;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }
}
