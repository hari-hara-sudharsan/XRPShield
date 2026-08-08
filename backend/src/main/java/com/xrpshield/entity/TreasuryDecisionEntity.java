package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "treasury_decisions")
public class TreasuryDecisionEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vault_id", nullable = false)
    private VaultEntity vault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private ConfidentialPolicyEntity policy;

    @Column(name = "decision_type", nullable = false, length = 40)
    private String decisionType; // NO_ACTION, PROTECT_POSITION, REDUCE_EXPOSURE, INCREASE_PROTECTION, REQUEST_REVIEW, EMERGENCY_EXIT

    @Column(name = "decision_version", nullable = false)
    private Integer decisionVersion = 1;


    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, EXPIRED

    @Column(name = "rationale", nullable = false, columnDefinition = "TEXT")
    private String rationale;

    @Column(name = "attestation_id", length = 100)
    private String attestationId;

    @Column(name = "decision_hash", nullable = false, length = 66)
    private String decisionHash;

    public TreasuryDecisionEntity() {}

    public TreasuryDecisionEntity(VaultEntity vault, ConfidentialPolicyEntity policy, String decisionType, Integer decisionVersion, String status, String rationale, String attestationId, String decisionHash) {
        this.vault = vault;
        this.policy = policy;
        this.decisionType = decisionType;
        this.decisionVersion = decisionVersion != null ? decisionVersion : 1;
        this.status = status != null ? status : "PENDING";
        this.rationale = rationale;
        this.attestationId = attestationId;
        this.decisionHash = decisionHash;
    }

    public VaultEntity getVault() {
        return vault;
    }

    public void setVault(VaultEntity vault) {
        this.vault = vault;
    }

    public ConfidentialPolicyEntity getPolicy() {
        return policy;
    }

    public void setPolicy(ConfidentialPolicyEntity policy) {
        this.policy = policy;
    }

    public String getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(String decisionType) {
        this.decisionType = decisionType;
    }

    public Integer getDecisionVersion() {
        return decisionVersion;
    }

    public void setDecisionVersion(Integer decisionVersion) {
        this.decisionVersion = decisionVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    public String getAttestationId() {
        return attestationId;
    }

    public void setAttestationId(String attestationId) {
        this.attestationId = attestationId;
    }

    public String getDecisionHash() {
        return decisionHash;
    }

    public void setDecisionHash(String decisionHash) {
        this.decisionHash = decisionHash;
    }
}
