package com.xrpshield.dto;

import java.util.UUID;

public class ExplainDecisionRequestDto {

    private UUID decisionId;
    private String decisionType;

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
}

