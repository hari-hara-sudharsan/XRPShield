package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "execution_results")
public class ExecutionResultEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "execution_id", nullable = false)
    private TreasuryExecutionEntity execution;

    @Column(name = "result_code", nullable = false, length = 50)
    private String resultCode;

    @Column(name = "result_payload", nullable = false, columnDefinition = "TEXT")
    private String resultPayload;

    @Column(name = "fcc_latency_ms", nullable = false)
    private Long fccLatencyMs = 0L;

    @Column(name = "blockchain_confirmation_ms", nullable = false)
    private Long blockchainConfirmationMs = 0L;

    public ExecutionResultEntity() {}

    public ExecutionResultEntity(TreasuryExecutionEntity execution, String resultCode, String resultPayload, Long fccLatencyMs, Long blockchainConfirmationMs) {
        this.execution = execution;
        this.resultCode = resultCode;
        this.resultPayload = resultPayload;
        this.fccLatencyMs = fccLatencyMs != null ? fccLatencyMs : 0L;
        this.blockchainConfirmationMs = blockchainConfirmationMs != null ? blockchainConfirmationMs : 0L;
    }

    public TreasuryExecutionEntity getExecution() {
        return execution;
    }

    public void setExecution(TreasuryExecutionEntity execution) {
        this.execution = execution;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultPayload() {
        return resultPayload;
    }

    public void setResultPayload(String resultPayload) {
        this.resultPayload = resultPayload;
    }

    public Long getFccLatencyMs() {
        return fccLatencyMs;
    }

    public void setFccLatencyMs(Long fccLatencyMs) {
        this.fccLatencyMs = fccLatencyMs;
    }

    public Long getBlockchainConfirmationMs() {
        return blockchainConfirmationMs;
    }

    public void setBlockchainConfirmationMs(Long blockchainConfirmationMs) {
        this.blockchainConfirmationMs = blockchainConfirmationMs;
    }
}
