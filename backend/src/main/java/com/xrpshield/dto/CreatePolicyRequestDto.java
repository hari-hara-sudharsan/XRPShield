package com.xrpshield.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public class CreatePolicyRequestDto {

    @NotNull(message = "Vault ID is required")
    private UUID vaultId;

    @NotBlank(message = "Policy name is required")
    private String name;

    private String asset = "FXRP";

    private BigDecimal riskThreshold = new BigDecimal("0.15");

    private BigDecimal maxExposure = new BigDecimal("100000.00");

    private BigDecimal maxDrawdown = new BigDecimal("0.10");

    private BigDecimal maxPositionSize = new BigDecimal("50000.00");

    private BigDecimal leverageLimit = new BigDecimal("1.0");

    private String stopCondition = "DRAWDOWN_EXCEEDED_10_PERCENT";

    private String takeProfitCondition = "YIELD_TARGET_8_PERCENT";

    private String emergencyExit = "AUTOMATIC_FREEZE";

    private String publicMetadata;

    public UUID getVaultId() {
        return vaultId;
    }

    public void setVaultId(UUID vaultId) {
        this.vaultId = vaultId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public BigDecimal getRiskThreshold() {
        return riskThreshold;
    }

    public void setRiskThreshold(BigDecimal riskThreshold) {
        this.riskThreshold = riskThreshold;
    }

    public BigDecimal getMaxExposure() {
        return maxExposure;
    }

    public void setMaxExposure(BigDecimal maxExposure) {
        this.maxExposure = maxExposure;
    }

    public BigDecimal getMaxDrawdown() {
        return maxDrawdown;
    }

    public void setMaxDrawdown(BigDecimal maxDrawdown) {
        this.maxDrawdown = maxDrawdown;
    }

    public BigDecimal getMaxPositionSize() {
        return maxPositionSize;
    }

    public void setMaxPositionSize(BigDecimal maxPositionSize) {
        this.maxPositionSize = maxPositionSize;
    }

    public BigDecimal getLeverageLimit() {
        return leverageLimit;
    }

    public void setLeverageLimit(BigDecimal leverageLimit) {
        this.leverageLimit = leverageLimit;
    }

    public String getStopCondition() {
        return stopCondition;
    }

    public void setStopCondition(String stopCondition) {
        this.stopCondition = stopCondition;
    }

    public String getTakeProfitCondition() {
        return takeProfitCondition;
    }

    public void setTakeProfitCondition(String takeProfitCondition) {
        this.takeProfitCondition = takeProfitCondition;
    }

    public String getEmergencyExit() {
        return emergencyExit;
    }

    public void setEmergencyExit(String emergencyExit) {
        this.emergencyExit = emergencyExit;
    }

    public String getPublicMetadata() {
        return publicMetadata;
    }

    public void setPublicMetadata(String publicMetadata) {
        this.publicMetadata = publicMetadata;
    }
}
