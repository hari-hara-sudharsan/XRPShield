package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "confidential_policies")
public class ConfidentialPolicyEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vault_id", nullable = false)
    private VaultEntity vault;

    @Column(name = "policy_name", nullable = false, length = 100)
    private String policyName;

    @Column(name = "policy_version", nullable = false)
    private Integer policyVersion = 1;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "public_metadata", columnDefinition = "TEXT")
    private String publicMetadata;

    @Column(name = "policy_hash", nullable = false, length = 66)
    private String policyHash;

    public ConfidentialPolicyEntity() {}

    public ConfidentialPolicyEntity(VaultEntity vault, String policyName, Integer policyVersion, String status, String publicMetadata, String policyHash) {
        this.vault = vault;
        this.policyName = policyName;
        this.policyVersion = policyVersion != null ? policyVersion : 1;
        this.status = status != null ? status : "ACTIVE";
        this.publicMetadata = publicMetadata;
        this.policyHash = policyHash;
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

    public Integer getPolicyVersion() {
        return policyVersion;
    }

    public void setPolicyVersion(Integer policyVersion) {
        this.policyVersion = policyVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPublicMetadata() {
        return publicMetadata;
    }

    public void setPublicMetadata(String publicMetadata) {
        this.publicMetadata = publicMetadata;
    }

    public String getPolicyHash() {
        return policyHash;
    }

    public void setPolicyHash(String policyHash) {
        this.policyHash = policyHash;
    }
}
