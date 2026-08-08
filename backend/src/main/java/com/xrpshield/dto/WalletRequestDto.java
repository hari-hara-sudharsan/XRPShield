package com.xrpshield.dto;

import com.xrpshield.entity.WalletType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class WalletRequestDto {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Wallet address is required")
    @Size(min = 24, max = 64, message = "Wallet address must be between 24 and 64 characters")
    private String address;

    @NotNull(message = "Wallet type is required")
    private WalletType walletType;

    private boolean isPrimary;

    public WalletRequestDto() {}

    public WalletRequestDto(UUID userId, String address, WalletType walletType, boolean isPrimary) {
        this.userId = userId;
        this.address = address;
        this.walletType = walletType;
        this.isPrimary = isPrimary;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public WalletType getWalletType() {
        return walletType;
    }

    public void setWalletType(WalletType walletType) {
        this.walletType = walletType;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }
}
