package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "decision_audits")
public class DecisionAuditEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "decision_id", nullable = false)
    private TreasuryDecisionEntity decision;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "actor", nullable = false, length = 64)
    private String actor;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    public DecisionAuditEntity() {}

    public DecisionAuditEntity(TreasuryDecisionEntity decision, String eventType, String actor, String details) {
        this.decision = decision;
        this.eventType = eventType;
        this.actor = actor;
        this.details = details;
    }

    public TreasuryDecisionEntity getDecision() {
        return decision;
    }

    public void setDecision(TreasuryDecisionEntity decision) {
        this.decision = decision;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
