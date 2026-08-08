package com.xrpshield.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "treasury_executions")
public class TreasuryExecutionEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "decision_id", nullable = false)
    private TreasuryDecisionEntity decision;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vault_id", nullable = false)
    private VaultEntity vault;

    @Column(name = "execution_state", nullable = false, length = 30)
    private String executionState = "PENDING"; // PENDING, QUEUED, VALIDATING, EXECUTING, COMPLETED, FAILED, CANCELLED, EXPIRED

    @Column(name = "tx_hash", length = 66)
    private String txHash;

    @Column(name = "block_number")
    private Long blockNumber;

    @Column(name = "gas_used")
    private Long gasUsed;

    @Column(name = "execution_hash", nullable = false, length = 66)
    private String executionHash;

    @Column(name = "completed_at")
    private Instant completedAt;

    public TreasuryExecutionEntity() {}

    public TreasuryExecutionEntity(TreasuryDecisionEntity decision, VaultEntity vault, String executionState, String txHash, String executionHash) {
        this.decision = decision;
        this.vault = vault;
        this.executionState = executionState != null ? executionState : "PENDING";
        this.txHash = txHash;
        this.executionHash = executionHash;
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

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
