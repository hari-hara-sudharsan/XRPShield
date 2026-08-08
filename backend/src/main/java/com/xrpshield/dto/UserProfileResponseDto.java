package com.xrpshield.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class UserProfileResponseDto {

    private UUID userId;
    private String email;
    private String displayName;
    private String role;
    private String status;
    private List<WalletResponseDto> wallets;
    private Instant createdAt;

    public UserProfileResponseDto() {}

    public UserProfileResponseDto(UUID userId, String email, String displayName, String role, String status, List<WalletResponseDto> wallets, Instant createdAt) {
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this.status = status;
        this.wallets = wallets;
        this.createdAt = createdAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<WalletResponseDto> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletResponseDto> wallets) {
        this.wallets = wallets;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
