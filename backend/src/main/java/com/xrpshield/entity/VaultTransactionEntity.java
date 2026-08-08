package com.xrpshield.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "vault_transactions")
public class VaultTransactionEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vault_id", nullable = false)
    private VaultEntity vault;

    @Column(name = "tx_type", nullable = false, length = 20)
    private String txType; // DEPOSIT, WITHDRAWAL, TRANSFER

    @Column(name = "amount", nullable = false, precision = 38, scale = 18)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 20)
    private String currency = "FXRP";

    @Column(name = "tx_hash", length = 66)
    private String txHash;

    @Column(name = "from_address", nullable = false, length = 64)
    private String fromAddress;

    @Column(name = "to_address", nullable = false, length = 64)
    private String toAddress;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "CONFIRMED";

    public VaultTransactionEntity() {}

    public VaultTransactionEntity(VaultEntity vault, String txType, BigDecimal amount, String currency, String txHash, String fromAddress, String toAddress, String status) {
        this.vault = vault;
        this.txType = txType;
        this.amount = amount;
        this.currency = currency != null ? currency : "FXRP";
        this.txHash = txHash;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.status = status != null ? status : "CONFIRMED";
    }

    public VaultEntity getVault() {
        return vault;
    }

    public void setVault(VaultEntity vault) {
        this.vault = vault;
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
}
