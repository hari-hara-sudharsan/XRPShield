package com.xrpshield.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class UpdatePolicyRequestDto {

    @NotBlank(message = "Policy name is required")
    private String name;

    private BigDecimal riskThreshold;

    private BigDecimal maxDrawdown;

    private String stopCondition;

    private String status = "ACTIVE";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getRiskThreshold() {
        return riskThreshold;
    }

    public void setRiskThreshold(BigDecimal riskThreshold) {
        this.riskThreshold = riskThreshold;
    }

    public BigDecimal getMaxDrawdown() {
        return maxDrawdown;
    }

    public void setMaxDrawdown(BigDecimal maxDrawdown) {
        this.maxDrawdown = maxDrawdown;
    }

    public String getStopCondition() {
        return stopCondition;
    }

    public void setStopCondition(String stopCondition) {
        this.stopCondition = stopCondition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
