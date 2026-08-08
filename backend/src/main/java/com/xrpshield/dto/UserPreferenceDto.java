package com.xrpshield.dto;

public class UserPreferenceDto {

    private String aiModel;
    private String explanationVerbosity;

    public UserPreferenceDto() {}

    public UserPreferenceDto(String aiModel, String explanationVerbosity) {
        this.aiModel = aiModel;
        this.explanationVerbosity = explanationVerbosity;
    }

    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }

    public String getExplanationVerbosity() {
        return explanationVerbosity;
    }

    public void setExplanationVerbosity(String explanationVerbosity) {
        this.explanationVerbosity = explanationVerbosity;
    }
}
