package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "platform_notifications")
public class PlatformNotificationEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "severity", nullable = false, length = 20)
    private String severity = "INFO"; // INFO, WARNING, CRITICAL

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    public PlatformNotificationEntity() {}

    public PlatformNotificationEntity(UserEntity user, String severity, String title, String message, Boolean isRead) {
        this.user = user;
        this.severity = severity != null ? severity : "INFO";
        this.title = title;
        this.message = message;
        this.isRead = isRead != null ? isRead : false;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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
}
