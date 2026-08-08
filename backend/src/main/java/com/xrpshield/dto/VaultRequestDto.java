package com.xrpshield.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public class VaultRequestDto {

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;

    @NotBlank(message = "Vault name is required")
    @Size(max = 100, message = "Vault name cannot exceed 100 characters")
    private String vaultName;

    private String vaultAddress;

    private String assetType;

    private BigDecimal initialBalance;

    public VaultRequestDto() {}

    public VaultRequestDto(UUID ownerId, String vaultName, String vaultAddress, String assetType, BigDecimal initialBalance) {
        this.ownerId = ownerId;
        this.vaultName = vaultName;
        this.vaultAddress = vaultAddress;
        this.assetType = assetType;
        this.initialBalance = initialBalance;
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

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
