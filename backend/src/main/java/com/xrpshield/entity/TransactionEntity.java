package com.xrpshield.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
public class TransactionEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vault_id")
    private VaultEntity vault;

    @Column(name = "tx_hash", unique = true, length = 66)
    private String txHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 38, scale = 18)
    private BigDecimal amount;

    @Column(name = "asset", nullable = false, length = 20)
    private String asset = "FXRP";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "attestation_proof", columnDefinition = "TEXT")
    private String attestationProof;

    @Column(name = "block_number")
    private Long blockNumber;

    public TransactionEntity() {}

    public TransactionEntity(VaultEntity vault, String txHash, TransactionType transactionType, BigDecimal amount, String asset, TransactionStatus status, String attestationProof, Long blockNumber) {
        this.vault = vault;
        this.txHash = txHash;
        this.transactionType = transactionType;
        this.amount = amount;
        this.asset = asset != null ? asset : "FXRP";
        this.status = status != null ? status : TransactionStatus.PENDING;
        this.attestationProof = attestationProof;
        this.blockNumber = blockNumber;
    }

    public VaultEntity getVault() {
        return vault;
    }

    public void setVault(VaultEntity vault) {
        this.vault = vault;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getAttestationProof() {
        return attestationProof;
    }

    public void setAttestationProof(String attestationProof) {
        this.attestationProof = attestationProof;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }
}
