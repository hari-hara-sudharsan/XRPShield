package com.xrpshield.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "feature_flags")
public class FeatureFlagEntity extends BaseEntity {

    @Column(name = "flag_key", nullable = false, unique = true, length = 100)
    private String flagKey;

    @Column(name = "flag_name", nullable = false, length = 150)
    private String flagName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = true;

    public FeatureFlagEntity() {}

    public FeatureFlagEntity(String flagKey, String flagName, String description, boolean isEnabled) {
        this.flagKey = flagKey;
        this.flagName = flagName;
        this.description = description;
        this.isEnabled = isEnabled;
    }

    public String getFlagKey() {
        return flagKey;
    }

    public void setFlagKey(String flagKey) {
        this.flagKey = flagKey;
    }

    public String getFlagName() {
        return flagName;
    }

    public void setFlagName(String flagName) {
        this.flagName = flagName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
