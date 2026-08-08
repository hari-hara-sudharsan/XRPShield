package com.xrpshield.dto;

import com.xrpshield.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionRequestDto {

    private UUID vaultId;

    private String txHash;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String asset;

    private String attestationProof;

    private Long blockNumber;

    public TransactionRequestDto() {}

    public TransactionRequestDto(UUID vaultId, String txHash, TransactionType transactionType, BigDecimal amount, String asset, String attestationProof, Long blockNumber) {
        this.vaultId = vaultId;
        this.txHash = txHash;
        this.transactionType = transactionType;
        this.amount = amount;
        this.asset = asset;
        this.attestationProof = attestationProof;
        this.blockNumber = blockNumber;
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
