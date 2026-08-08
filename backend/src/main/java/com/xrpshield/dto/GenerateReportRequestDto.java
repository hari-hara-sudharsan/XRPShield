package com.xrpshield.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class GenerateReportRequestDto {

    private UUID vaultId;

    @NotBlank(message = "Report type is required")
    private String reportType; // EXECUTIVE, RISK_AUDIT, EXECUTION_SUMMARY


    public UUID getVaultId() {
        return vaultId;
    }

    public void setVaultId(UUID vaultId) {
        this.vaultId = vaultId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
}
