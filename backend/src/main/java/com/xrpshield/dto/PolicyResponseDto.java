package com.xrpshield.dto;

import java.time.Instant;
import java.util.UUID;

public class PolicyResponseDto {

    private UUID id;
    private UUID vaultId;
    private String vaultName;
    private String name;
    private Integer version;
    private String status;
    private String publicMetadata;
    private String policyHash;
    private String latestEvaluationStatus;
    private String latestAttestationStatus;
    private Instant createdAt;
    private Instant updatedAt;

    public PolicyResponseDto() {}

    public PolicyResponseDto(UUID id, UUID vaultId, String vaultName, String name, Integer version, String status, String publicMetadata, String policyHash, String latestEvaluationStatus, String latestAttestationStatus, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.vaultId = vaultId;
        this.vaultName = vaultName;
        this.name = name;
        this.version = version;
        this.status = status;
        this.publicMetadata = publicMetadata;
        this.policyHash = policyHash;
        this.latestEvaluationStatus = latestEvaluationStatus;
        this.latestAttestationStatus = latestAttestationStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getLatestEvaluationStatus() {
        return latestEvaluationStatus;
    }

    public void setLatestEvaluationStatus(String latestEvaluationStatus) {
        this.latestEvaluationStatus = latestEvaluationStatus;
    }

    public String getLatestAttestationStatus() {
        return latestAttestationStatus;
    }

    public void setLatestAttestationStatus(String latestAttestationStatus) {
        this.latestAttestationStatus = latestAttestationStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
