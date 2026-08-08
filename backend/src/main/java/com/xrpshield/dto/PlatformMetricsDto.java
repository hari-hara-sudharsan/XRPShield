package com.xrpshield.dto;

public class PlatformMetricsDto {

    private long usedMemoryMb;
    private long activeDbConnections;
    private double systemUptimePercentage;
    private long rpcLatencyMs;
    private long fccLatencyMs;
    private long avgExecutionConfirmationMs;
    private long activeAlertsCount;

    public PlatformMetricsDto() {}

    public PlatformMetricsDto(long usedMemoryMb, long activeDbConnections, double systemUptimePercentage, long rpcLatencyMs, long fccLatencyMs, long avgExecutionConfirmationMs, long activeAlertsCount) {
        this.usedMemoryMb = usedMemoryMb;
        this.activeDbConnections = activeDbConnections;
        this.systemUptimePercentage = systemUptimePercentage;
        this.rpcLatencyMs = rpcLatencyMs;
        this.fccLatencyMs = fccLatencyMs;
        this.avgExecutionConfirmationMs = avgExecutionConfirmationMs;
        this.activeAlertsCount = activeAlertsCount;
    }

    public long getUsedMemoryMb() {
        return usedMemoryMb;
    }

    public void setUsedMemoryMb(long usedMemoryMb) {
        this.usedMemoryMb = usedMemoryMb;
    }

    public long getActiveDbConnections() {
        return activeDbConnections;
    }

    public void setActiveDbConnections(long activeDbConnections) {
        this.activeDbConnections = activeDbConnections;
    }

    public double getSystemUptimePercentage() {
        return systemUptimePercentage;
    }

    public void setSystemUptimePercentage(double systemUptimePercentage) {
        this.systemUptimePercentage = systemUptimePercentage;
    }

    public long getRpcLatencyMs() {
        return rpcLatencyMs;
    }

    public void setRpcLatencyMs(long rpcLatencyMs) {
        this.rpcLatencyMs = rpcLatencyMs;
    }

    public long getFccLatencyMs() {
        return fccLatencyMs;
    }

    public void setFccLatencyMs(long fccLatencyMs) {
        this.fccLatencyMs = fccLatencyMs;
    }

    public long getAvgExecutionConfirmationMs() {
        return avgExecutionConfirmationMs;
    }

    public void setAvgExecutionConfirmationMs(long avgExecutionConfirmationMs) {
        this.avgExecutionConfirmationMs = avgExecutionConfirmationMs;
    }

    public long getActiveAlertsCount() {
        return activeAlertsCount;
    }

    public void setActiveAlertsCount(long activeAlertsCount) {
        this.activeAlertsCount = activeAlertsCount;
    }
}
