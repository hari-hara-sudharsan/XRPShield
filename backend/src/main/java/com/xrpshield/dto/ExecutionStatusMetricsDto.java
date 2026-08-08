package com.xrpshield.dto;

public class ExecutionStatusMetricsDto {

    private long totalExecutions;
    private long completedExecutions;
    private long failedExecutions;
    private long queueSize;
    private double successRatePercentage;
    private long avgConfirmationMs;

    public ExecutionStatusMetricsDto() {}

    public ExecutionStatusMetricsDto(long totalExecutions, long completedExecutions, long failedExecutions, long queueSize, double successRatePercentage, long avgConfirmationMs) {
        this.totalExecutions = totalExecutions;
        this.completedExecutions = completedExecutions;
        this.failedExecutions = failedExecutions;
        this.queueSize = queueSize;
        this.successRatePercentage = successRatePercentage;
        this.avgConfirmationMs = avgConfirmationMs;
    }

    public long getTotalExecutions() {
        return totalExecutions;
    }

    public void setTotalExecutions(long totalExecutions) {
        this.totalExecutions = totalExecutions;
    }

    public long getCompletedExecutions() {
        return completedExecutions;
    }

    public void setCompletedExecutions(long completedExecutions) {
        this.completedExecutions = completedExecutions;
    }

    public long getFailedExecutions() {
        return failedExecutions;
    }

    public void setFailedExecutions(long failedExecutions) {
        this.failedExecutions = failedExecutions;
    }

    public long getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(long queueSize) {
        this.queueSize = queueSize;
    }

    public double getSuccessRatePercentage() {
        return successRatePercentage;
    }

    public void setSuccessRatePercentage(double successRatePercentage) {
        this.successRatePercentage = successRatePercentage;
    }

    public long getAvgConfirmationMs() {
        return avgConfirmationMs;
    }

    public void setAvgConfirmationMs(long avgConfirmationMs) {
        this.avgConfirmationMs = avgConfirmationMs;
    }
}
