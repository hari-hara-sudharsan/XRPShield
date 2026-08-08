package com.xrpshield.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransactionResponseDto {

    private UUID id;
    private UUID vaultId;
    private String txHash;
    private String transactionType;
    private BigDecimal amount;
    private String asset;
    private String status;
    private String attestationProof;
    private Long blockNumber;
    private Instant createdAt;

    public TransactionResponseDto() {}

    public TransactionResponseDto(UUID id, UUID vaultId, String txHash, String transactionType, BigDecimal amount, String asset, String status, String attestationProof, Long blockNumber, Instant createdAt) {
        this.id = id;
        this.vaultId = vaultId;
        this.txHash = txHash;
        this.transactionType = transactionType;
        this.amount = amount;
        this.asset = asset;
        this.status = status;
        this.attestationProof = attestationProof;
        this.blockNumber = blockNumber;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getVaultId() {
        return vaultId;
    }

    public void setVaultId(UUID vaultId) {
        this.vaultId = vaultId;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
