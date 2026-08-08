package com.xrpshield.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "decision_evaluations")
public class DecisionEvaluationEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "decision_id", nullable = false)
    private TreasuryDecisionEntity decision;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vault_id", nullable = false)
    private VaultEntity vault;

    @Column(name = "fcc_latency_ms", nullable = false)
    private Long fccLatencyMs = 0L;

    @Column(name = "result_summary", columnDefinition = "TEXT")
    private String resultSummary;

    @Column(name = "evaluated_at", nullable = false)
    private Instant evaluatedAt = Instant.now();

    public DecisionEvaluationEntity() {}

    public DecisionEvaluationEntity(TreasuryDecisionEntity decision, VaultEntity vault, Long fccLatencyMs, String resultSummary) {
        this.decision = decision;
        this.vault = vault;
        this.fccLatencyMs = fccLatencyMs != null ? fccLatencyMs : 0L;
        this.resultSummary = resultSummary;
        this.evaluatedAt = Instant.now();
    }

    public TreasuryDecisionEntity getDecision() {
        return decision;
    }

    public void setDecision(TreasuryDecisionEntity decision) {
        this.decision = decision;
    }

    public VaultEntity getVault() {
        return vault;
    }

    public void setVault(VaultEntity vault) {
        this.vault = vault;
    }

    public Long getFccLatencyMs() {
        return fccLatencyMs;
    }

    public void setFccLatencyMs(Long fccLatencyMs) {
        this.fccLatencyMs = fccLatencyMs;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }

    public Instant getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(Instant evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }
}
