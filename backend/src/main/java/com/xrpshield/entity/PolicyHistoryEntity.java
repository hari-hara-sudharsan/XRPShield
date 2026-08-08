package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "policy_history")
public class PolicyHistoryEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private ConfidentialPolicyEntity policy;

    @Column(name = "policy_version", nullable = false)
    private Integer policyVersion;


    @Column(name = "changes_json", columnDefinition = "TEXT")
    private String changesJson;

    public PolicyHistoryEntity() {}

    public PolicyHistoryEntity(ConfidentialPolicyEntity policy, Integer policyVersion, String changesJson) {
        this.policy = policy;
        this.policyVersion = policyVersion;
        this.changesJson = changesJson;
    }

    public ConfidentialPolicyEntity getPolicy() {
        return policy;
    }

    public void setPolicy(ConfidentialPolicyEntity policy) {
        this.policy = policy;
    }

    public Integer getPolicyVersion() {
        return policyVersion;
    }

    public void setPolicyVersion(Integer policyVersion) {
        this.policyVersion = policyVersion;
    }

    public String getChangesJson() {
        return changesJson;
    }

    public void setChangesJson(String changesJson) {
        this.changesJson = changesJson;
    }
}
