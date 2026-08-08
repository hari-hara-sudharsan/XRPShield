package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "execution_audits")
public class ExecutionAuditEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "execution_id", nullable = false)
    private TreasuryExecutionEntity execution;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "actor", nullable = false, length = 64)
    private String actor;

    @Column(name = "tx_hash", length = 66)
    private String txHash;

    @Column(name = "wallet", length = 64)
    private String wallet;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    public ExecutionAuditEntity() {}

    public ExecutionAuditEntity(TreasuryExecutionEntity execution, String eventType, String actor, String txHash, String wallet, String details) {
        this.execution = execution;
        this.eventType = eventType;
        this.actor = actor;
        this.txHash = txHash;
        this.wallet = wallet;
        this.details = details;
    }

    public TreasuryExecutionEntity getExecution() {
        return execution;
    }

    public void setExecution(TreasuryExecutionEntity execution) {
        this.execution = execution;
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

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
