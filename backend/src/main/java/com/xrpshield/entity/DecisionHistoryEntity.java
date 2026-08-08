package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "decision_history")
public class DecisionHistoryEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "decision_id", nullable = false)
    private TreasuryDecisionEntity decision;

    @Column(name = "decision_version", nullable = false)
    private Integer decisionVersion;


    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "actor_address", nullable = false, length = 64)
    private String actorAddress;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    public DecisionHistoryEntity() {}

    public DecisionHistoryEntity(TreasuryDecisionEntity decision, Integer decisionVersion, String action, String actorAddress, String details) {
        this.decision = decision;
        this.decisionVersion = decisionVersion;
        this.action = action;
        this.actorAddress = actorAddress;
        this.details = details;
    }

    public TreasuryDecisionEntity getDecision() {
        return decision;
    }

    public void setDecision(TreasuryDecisionEntity decision) {
        this.decision = decision;
    }

    public Integer getDecisionVersion() {
        return decisionVersion;
    }

    public void setDecisionVersion(Integer decisionVersion) {
        this.decisionVersion = decisionVersion;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActorAddress() {
        return actorAddress;
    }

    public void setActorAddress(String actorAddress) {
        this.actorAddress = actorAddress;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
