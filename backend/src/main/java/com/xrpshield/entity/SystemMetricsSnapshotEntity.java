package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "system_metrics_snapshots")
public class SystemMetricsSnapshotEntity extends BaseEntity {

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "metric_value", nullable = false)
    private Double metricValue;

    @Column(name = "category", nullable = false, length = 50)
    private String category; // SYSTEM, DATABASE, BLOCKCHAIN, FCC, AI, API

    public SystemMetricsSnapshotEntity() {}

    public SystemMetricsSnapshotEntity(String metricName, Double metricValue, String category) {
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.category = category;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
