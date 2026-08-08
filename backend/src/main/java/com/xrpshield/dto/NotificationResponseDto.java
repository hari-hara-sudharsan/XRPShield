package com.xrpshield.dto;

import java.time.Instant;
import java.util.UUID;

public class NotificationResponseDto {

    private UUID id;
    private UUID userId;
    private String title;
    private String message;
    private String severity;
    private boolean isRead;
    private Instant createdAt;

    public NotificationResponseDto() {}

    public NotificationResponseDto(UUID id, UUID userId, String title, String message, String severity, boolean isRead, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.severity = severity;
        this.isRead = isRead;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
