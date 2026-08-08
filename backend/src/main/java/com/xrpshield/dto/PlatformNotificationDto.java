package com.xrpshield.dto;

import java.time.Instant;
import java.util.UUID;

public class PlatformNotificationDto {

    private UUID id;
    private String severity;
    private String title;
    private String message;
    private Boolean isRead;
    private Instant createdAt;

    public PlatformNotificationDto() {}

    public PlatformNotificationDto(UUID id, String severity, String title, String message, Boolean isRead, Instant createdAt) {
        this.id = id;
        this.severity = severity;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
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

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
