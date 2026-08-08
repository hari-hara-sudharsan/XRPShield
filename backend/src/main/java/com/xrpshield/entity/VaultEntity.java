package com.xrpshield.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vaults")
public class VaultEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(name = "vault_name", nullable = false, length = 100)
    private String vaultName;

    @Column(name = "vault_address", unique = true, length = 64)
    private String vaultAddress;

    @Column(name = "asset_type", nullable = false, length = 20)
    private String assetType = "FXRP";

    @Column(name = "balance", nullable = false, precision = 38, scale = 18)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VaultStatus status = VaultStatus.ACTIVE;

    @OneToMany(mappedBy = "vault", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VaultPolicyEntity> policies = new ArrayList<>();

    @OneToMany(mappedBy = "vault")
    private List<TransactionEntity> transactions = new ArrayList<>();

    public VaultEntity() {}

    public VaultEntity(UserEntity owner, String vaultName, String vaultAddress, String assetType, BigDecimal balance, VaultStatus status) {
        this.owner = owner;
        this.vaultName = vaultName;
        this.vaultAddress = vaultAddress;
        this.assetType = assetType != null ? assetType : "FXRP";
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.status = status != null ? status : VaultStatus.ACTIVE;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public String getVaultName() {
        return vaultName;
    }

    public void setVaultName(String vaultName) {
        this.vaultName = vaultName;
    }

    public String getVaultAddress() {
        return vaultAddress;
    }

    public void setVaultAddress(String vaultAddress) {
        this.vaultAddress = vaultAddress;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public VaultStatus getStatus() {
        return status;
    }

    public void setStatus(VaultStatus status) {
        this.status = status;
    }

    public List<VaultPolicyEntity> getPolicies() {
        return policies;
    }

    public void setPolicies(List<VaultPolicyEntity> policies) {
        this.policies = policies;
    }

    public List<TransactionEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
    }
}
