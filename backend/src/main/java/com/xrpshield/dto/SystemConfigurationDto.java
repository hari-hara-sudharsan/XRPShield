package com.xrpshield.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public class SystemConfigurationDto {

    private UUID id;

    @NotBlank(message = "Configuration key is required")
    @Size(max = 100, message = "Key cannot exceed 100 characters")
    private String configKey;

    @NotBlank(message = "Configuration value is required")
    private String configValue;

    private String description;

    private Instant createdAt;
    private Instant updatedAt;

    public SystemConfigurationDto() {}

    public SystemConfigurationDto(UUID id, String configKey, String configValue, String description, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.configKey = configKey;
        this.configValue = configValue;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
