package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "execution_metadata")
public class ExecutionMetadataEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "execution_id", nullable = false)
    private TreasuryExecutionEntity execution;

    @Column(name = "meta_key", nullable = false, length = 100)
    private String metaKey;

    @Column(name = "meta_value", nullable = false, columnDefinition = "TEXT")
    private String metaValue;

    public ExecutionMetadataEntity() {}

    public ExecutionMetadataEntity(TreasuryExecutionEntity execution, String metaKey, String metaValue) {
        this.execution = execution;
        this.metaKey = metaKey;
        this.metaValue = metaValue;
    }

    public TreasuryExecutionEntity getExecution() {
        return execution;
    }

    public void setExecution(TreasuryExecutionEntity execution) {
        this.execution = execution;
    }

    public String getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
    }

    public String getMetaValue() {
        return metaValue;
    }

    public void setMetaValue(String metaValue) {
        this.metaValue = metaValue;
    }
}
