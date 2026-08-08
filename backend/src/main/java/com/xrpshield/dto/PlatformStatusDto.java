package com.xrpshield.dto;

import java.time.Instant;
import java.util.List;

public class PlatformStatusDto {

    private String overallStatus;
    private Instant timestamp;
    private List<ComponentHealthDto> components;

    public PlatformStatusDto() {}

    public PlatformStatusDto(String overallStatus, Instant timestamp, List<ComponentHealthDto> components) {
        this.overallStatus = overallStatus;
        this.timestamp = timestamp;
        this.components = components;
    }

    public String getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public List<ComponentHealthDto> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentHealthDto> components) {
        this.components = components;
    }
}
