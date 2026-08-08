package com.xrpshield.dto;

import java.time.Instant;
import java.util.UUID;

public class DecisionResponseDto {

    private UUID id;
    private UUID vaultId;
    private String vaultName;
    private UUID policyId;
    private String policyName;
    private String decisionType;
    private Integer version;
    private String status;
    private String rationale;
    private String attestationId;
    private String decisionHash;
    private Instant createdAt;

    public DecisionResponseDto() {}

    public DecisionResponseDto(UUID id, UUID vaultId, String vaultName, UUID policyId, String policyName, String decisionType, Integer version, String status, String rationale, String attestationId, String decisionHash, Instant createdAt) {
        this.id = id;
        this.vaultId = vaultId;
        this.vaultName = vaultName;
        this.policyId = policyId;
        this.policyName = policyName;
        this.decisionType = decisionType;
        this.version = version;
        this.status = status;
        this.rationale = rationale;
        this.attestationId = attestationId;
        this.decisionHash = decisionHash;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getVaultId() {
        return vaultId;
    }

    public void setVaultId(UUID vaultId) {
        this.vaultId = vaultId;
    }

    public String getVaultName() {
        return vaultName;
    }

    public void setVaultName(String vaultName) {
        this.vaultName = vaultName;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public void setPolicyId(UUID policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(String decisionType) {
        this.decisionType = decisionType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
