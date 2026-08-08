package com.xrpshield.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vault_policies")
public class VaultPolicyEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vault_id", nullable = false)
    private VaultEntity vault;

    @Column(name = "policy_name", nullable = false, length = 100)
    private String policyName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "confidential_hash", length = 66)
    private String confidentialHash;

    @Column(name = "execution_trigger", nullable = false, length = 50)
    private String executionTrigger;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PolicyStatus status = PolicyStatus.ACTIVE;

    public VaultPolicyEntity() {}

    public VaultPolicyEntity(VaultEntity vault, String policyName, String description, String confidentialHash, String executionTrigger, PolicyStatus status) {
        this.vault = vault;
        this.policyName = policyName;
        this.description = description;
        this.confidentialHash = confidentialHash;
        this.executionTrigger = executionTrigger;
        this.status = status != null ? status : PolicyStatus.ACTIVE;
    }

    public VaultEntity getVault() {
        return vault;
    }

    public void setVault(VaultEntity vault) {
        this.vault = vault;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfidentialHash() {
        return confidentialHash;
    }

    public void setConfidentialHash(String confidentialHash) {
        this.confidentialHash = confidentialHash;
    }

    public String getExecutionTrigger() {
        return executionTrigger;
    }

    public void setExecutionTrigger(String executionTrigger) {
        this.executionTrigger = executionTrigger;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }
}
