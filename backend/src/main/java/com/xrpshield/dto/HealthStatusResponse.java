package com.xrpshield.dto;

import java.time.Instant;
import java.util.Map;

public class HealthStatusResponse {

    private String status;
    private String applicationName;
    private String version;
    private String environment;
    private Instant timestamp;
    private Map<String, Object> components;

    public HealthStatusResponse() {
        this.timestamp = Instant.now();
    }

    public HealthStatusResponse(String status, String applicationName, String version, String environment, Instant timestamp, Map<String, Object> components) {
        this.status = status;
        this.applicationName = applicationName;
        this.version = version;
        this.environment = environment;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
        this.components = components;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getComponents() {
        return components;
    }

    public void setComponents(Map<String, Object> components) {
        this.components = components;
    }
}
