package com.xrpshield.dto;

import java.time.Instant;
import java.util.UUID;

public class WalletResponseDto {

    private UUID id;
    private UUID userId;
    private String address;
    private String walletType;
    private boolean isPrimary;
    private String status;
    private Instant createdAt;

    public WalletResponseDto() {}

    public WalletResponseDto(UUID id, UUID userId, String address, String walletType, boolean isPrimary, String status, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.address = address;
        this.walletType = walletType;
        this.isPrimary = isPrimary;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getWalletType() {
        return walletType;
    }

    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
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
