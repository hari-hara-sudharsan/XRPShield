package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "decision_metadata")
public class DecisionMetadataEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "decision_id", nullable = false)
    private TreasuryDecisionEntity decision;

    @Column(name = "meta_key", nullable = false, length = 100)
    private String metaKey;

    @Column(name = "meta_value", nullable = false, columnDefinition = "TEXT")
    private String metaValue;

    public DecisionMetadataEntity() {}

    public DecisionMetadataEntity(TreasuryDecisionEntity decision, String metaKey, String metaValue) {
        this.decision = decision;
        this.metaKey = metaKey;
        this.metaValue = metaValue;
    }

    public TreasuryDecisionEntity getDecision() {
        return decision;
    }

    public void setDecision(TreasuryDecisionEntity decision) {
        this.decision = decision;
    }

    public String getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
    }

    public String getMetaValue() {
        return metaValue;
    }

    public void setMetaValue(String metaValue) {
        this.metaValue = metaValue;
    }
}
