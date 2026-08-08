package com.xrpshield.dto;

import java.time.Instant;
import java.util.UUID;

public class SessionResponseDto {

    private UUID id;
    private UUID userId;
    private String sessionToken;
    private String nonce;
    private String status;
    private Instant expiresAt;
    private Instant createdAt;

    public SessionResponseDto() {}

    public SessionResponseDto(UUID id, UUID userId, String sessionToken, String nonce, String status, Instant expiresAt, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.sessionToken = sessionToken;
        this.nonce = nonce;
        this.status = status;
        this.expiresAt = expiresAt;
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

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
