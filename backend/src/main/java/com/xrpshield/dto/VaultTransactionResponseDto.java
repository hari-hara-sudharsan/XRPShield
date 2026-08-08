package com.xrpshield.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class VaultTransactionResponseDto {

    private UUID id;
    private UUID vaultId;
    private String txType;
    private BigDecimal amount;
    private String currency;
    private String txHash;
    private String fromAddress;
    private String toAddress;
    private String status;
    private Instant createdAt;

    public VaultTransactionResponseDto() {}

    public VaultTransactionResponseDto(UUID id, UUID vaultId, String txType, BigDecimal amount, String currency, String txHash, String fromAddress, String toAddress, String status, Instant createdAt) {
        this.id = id;
        this.vaultId = vaultId;
        this.txType = txType;
        this.amount = amount;
        this.currency = currency;
        this.txHash = txHash;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.status = status;
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

    public String getTxType() {
        return txType;
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
