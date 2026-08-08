package com.xrpshield.dto;

public class DecisionStatusMetricsDto {

    private long totalDecisions;
    private long pendingDecisions;
    private long approvedDecisions;
    private long queueSize;
    private long avgDecisionLatencyMs;
    private long avgFccLatencyMs;

    public DecisionStatusMetricsDto() {}

    public DecisionStatusMetricsDto(long totalDecisions, long pendingDecisions, long approvedDecisions, long queueSize, long avgDecisionLatencyMs, long avgFccLatencyMs) {
        this.totalDecisions = totalDecisions;
        this.pendingDecisions = pendingDecisions;
        this.approvedDecisions = approvedDecisions;
        this.queueSize = queueSize;
        this.avgDecisionLatencyMs = avgDecisionLatencyMs;
        this.avgFccLatencyMs = avgFccLatencyMs;
    }

    public long getTotalDecisions() {
        return totalDecisions;
    }

    public void setTotalDecisions(long totalDecisions) {
        this.totalDecisions = totalDecisions;
    }

    public long getPendingDecisions() {
        return pendingDecisions;
    }

    public void setPendingDecisions(long pendingDecisions) {
        this.pendingDecisions = pendingDecisions;
    }

    public long getApprovedDecisions() {
        return approvedDecisions;
    }

    public void setApprovedDecisions(long approvedDecisions) {
        this.approvedDecisions = approvedDecisions;
    }

    public long getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(long queueSize) {
        this.queueSize = queueSize;
    }

    public long getAvgDecisionLatencyMs() {
        return avgDecisionLatencyMs;
    }

    public void setAvgDecisionLatencyMs(long avgDecisionLatencyMs) {
        this.avgDecisionLatencyMs = avgDecisionLatencyMs;
    }

    public long getAvgFccLatencyMs() {
        return avgFccLatencyMs;
    }

    public void setAvgFccLatencyMs(long avgFccLatencyMs) {
        this.avgFccLatencyMs = avgFccLatencyMs;
    }
}
