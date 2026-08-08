package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_preferences")
public class UserPreferenceEntity extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "ai_model", nullable = false, length = 50)
    private String aiModel = "gpt-4o";

    @Column(name = "explanation_verbosity", nullable = false, length = 30)
    private String explanationVerbosity = "BALANCED"; // CONCISE, BALANCED, DETAILED

    public UserPreferenceEntity() {}

    public UserPreferenceEntity(UserEntity user, String aiModel, String explanationVerbosity) {
        this.user = user;
        this.aiModel = aiModel != null ? aiModel : "gpt-4o";
        this.explanationVerbosity = explanationVerbosity != null ? explanationVerbosity : "BALANCED";
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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
