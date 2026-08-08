package com.xrpshield.dto;

import java.time.Instant;
import java.util.UUID;

public class ExecutionResponseDto {

    private UUID id;
    private UUID decisionId;
    private String decisionType;
    private UUID vaultId;
    private String vaultName;
    private String executionState;
    private String txHash;
    private Long blockNumber;
    private Long gasUsed;
    private String executionHash;
    private Instant createdAt;
    private Instant completedAt;

    public ExecutionResponseDto() {}

    public ExecutionResponseDto(UUID id, UUID decisionId, String decisionType, UUID vaultId, String vaultName, String executionState, String txHash, Long blockNumber, Long gasUsed, String executionHash, Instant createdAt, Instant completedAt) {
        this.id = id;
        this.decisionId = decisionId;
        this.decisionType = decisionType;
        this.vaultId = vaultId;
        this.vaultName = vaultName;
        this.executionState = executionState;
        this.txHash = txHash;
        this.blockNumber = blockNumber;
        this.gasUsed = gasUsed;
        this.executionHash = executionHash;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(UUID decisionId) {
        this.decisionId = decisionId;
    }

    public String getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(String decisionType) {
        this.decisionType = decisionType;
    }

    public UUID getVaultId() {
        return vaultId;
    }

    public void setVaultId(UUID vaultId) {
        this.vaultId = vaultId;
    }

    public String getVaultName() {
        return vaultName;
    }

    public void setVaultName(String vaultName) {
        this.vaultName = vaultName;
    }

    public String getExecutionState() {
        return executionState;
    }

    public void setExecutionState(String executionState) {
        this.executionState = executionState;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public Long getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(Long gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getExecutionHash() {
        return executionHash;
    }

    public void setExecutionHash(String executionHash) {
        this.executionHash = executionHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
