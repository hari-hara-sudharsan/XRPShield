package com.xrpshield.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class StartExecutionRequestDto {

    @NotNull(message = "Decision ID is required")
    private UUID decisionId;

    private String notes;

    public UUID getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(UUID decisionId) {
        this.decisionId = decisionId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
