package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "execution_history")
public class ExecutionHistoryEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "execution_id", nullable = false)
    private TreasuryExecutionEntity execution;

    @Column(name = "state", nullable = false, length = 30)
    private String state;

    @Column(name = "actor", nullable = false, length = 64)
    private String actor;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    public ExecutionHistoryEntity() {}

    public ExecutionHistoryEntity(TreasuryExecutionEntity execution, String state, String actor, String details) {
        this.execution = execution;
        this.state = state;
        this.actor = actor;
        this.details = details;
    }

    public TreasuryExecutionEntity getExecution() {
        return execution;
    }

    public void setExecution(TreasuryExecutionEntity execution) {
        this.execution = execution;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
