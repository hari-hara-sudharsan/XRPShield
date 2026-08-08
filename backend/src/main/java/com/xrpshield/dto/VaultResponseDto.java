package com.xrpshield.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class VaultResponseDto {

    private UUID id;
    private UUID ownerId;
    private String vaultName;
    private String vaultAddress;
    private String assetType;
    private BigDecimal balance;
    private String status;
    private Instant createdAt;

    public VaultResponseDto() {}

    public VaultResponseDto(UUID id, UUID ownerId, String vaultName, String vaultAddress, String assetType, BigDecimal balance, String status, Instant createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.vaultName = vaultName;
        this.vaultAddress = vaultAddress;
        this.assetType = assetType;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
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
