package com.xrpshield.dto;

import java.math.BigDecimal;

public class CanonicalPolicyPayloadDto {
    private String vaultAddress;
    private String asset;
    private BigDecimal hedgeRatio;
    private BigDecimal triggerThreshold;
    private BigDecimal maximumProtection;
    private Long deadline;
    private Long nonce;
    private Long policyVersion;

    public CanonicalPolicyPayloadDto() {}

    public CanonicalPolicyPayloadDto(String vaultAddress, String asset, BigDecimal hedgeRatio, BigDecimal triggerThreshold, BigDecimal maximumProtection, Long deadline, Long nonce, Long policyVersion) {
        this.vaultAddress = vaultAddress;
        this.asset = asset;
        this.hedgeRatio = hedgeRatio;
        this.triggerThreshold = triggerThreshold;
        this.maximumProtection = maximumProtection;
        this.deadline = deadline;
        this.nonce = nonce;
        this.policyVersion = policyVersion;
    }

    public String getVaultAddress() {
        return vaultAddress;
    }

    public void setVaultAddress(String vaultAddress) {
        this.vaultAddress = vaultAddress;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public BigDecimal getHedgeRatio() {
        return hedgeRatio;
    }

    public void setHedgeRatio(BigDecimal hedgeRatio) {
        this.hedgeRatio = hedgeRatio;
    }

    public BigDecimal getTriggerThreshold() {
        return triggerThreshold;
    }

    public void setTriggerThreshold(BigDecimal triggerThreshold) {
        this.triggerThreshold = triggerThreshold;
    }

    public BigDecimal getMaximumProtection() {
        return maximumProtection;
    }

    public void setMaximumProtection(BigDecimal maximumProtection) {
        this.maximumProtection = maximumProtection;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public Long getPolicyVersion() {
        return policyVersion;
    }

    public void setPolicyVersion(Long policyVersion) {
        this.policyVersion = policyVersion;
    }
}
