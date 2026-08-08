package com.xrpshield.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "vault_balances")
public class VaultBalanceEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vault_id", nullable = false)
    private VaultEntity vault;

    @Column(name = "currency", nullable = false, length = 20)
    private String currency = "FXRP";

    @Column(name = "balance_amount", nullable = false, precision = 38, scale = 18)
    private BigDecimal balanceAmount = BigDecimal.ZERO;

    @Column(name = "locked_amount", nullable = false, precision = 38, scale = 18)
    private BigDecimal lockedAmount = BigDecimal.ZERO;

    public VaultBalanceEntity() {}

    public VaultBalanceEntity(VaultEntity vault, String currency, BigDecimal balanceAmount, BigDecimal lockedAmount) {
        this.vault = vault;
        this.currency = currency != null ? currency : "FXRP";
        this.balanceAmount = balanceAmount != null ? balanceAmount : BigDecimal.ZERO;
        this.lockedAmount = lockedAmount != null ? lockedAmount : BigDecimal.ZERO;
    }

    public VaultEntity getVault() {
        return vault;
    }

    public void setVault(VaultEntity vault) {
        this.vault = vault;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public BigDecimal getLockedAmount() {
        return lockedAmount;
    }

    public void setLockedAmount(BigDecimal lockedAmount) {
        this.lockedAmount = lockedAmount;
    }
}
