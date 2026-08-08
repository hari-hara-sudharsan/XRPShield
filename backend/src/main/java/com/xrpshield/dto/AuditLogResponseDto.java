package com.xrpshield.dto;

import java.time.Instant;
import java.util.UUID;

public class AuditLogResponseDto {

    private UUID id;
    private UUID userId;
    private String action;
    private String resource;
    private String details;
    private String ipAddress;
    private Instant createdAt;

    public AuditLogResponseDto() {}

    public AuditLogResponseDto(UUID id, UUID userId, String action, String resource, String details, String ipAddress, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.resource = resource;
        this.details = details;
        this.ipAddress = ipAddress;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
