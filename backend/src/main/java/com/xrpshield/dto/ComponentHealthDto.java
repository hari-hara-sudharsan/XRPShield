package com.xrpshield.dto;

public class ComponentHealthDto {

    private String name;
    private String status; // UP, DOWN, DEGRADED
    private Long latencyMs;
    private String details;

    public ComponentHealthDto() {}

    public ComponentHealthDto(String name, String status, Long latencyMs, String details) {
        this.name = name;
        this.status = status;
        this.latencyMs = latencyMs;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Long latencyMs) {
        this.latencyMs = latencyMs;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
