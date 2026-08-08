package com.xrpshield.dto;

import java.time.Instant;
import java.util.UUID;

public class PolicyAttestationResponseDto {

    private UUID id;
    private UUID policyId;
    private String attestationId;
    private String enclaveQuoteHash;
    private String verificationStatus;
    private Instant attestedAt;

    public PolicyAttestationResponseDto() {}

    public PolicyAttestationResponseDto(UUID id, UUID policyId, String attestationId, String enclaveQuoteHash, String verificationStatus, Instant attestedAt) {
        this.id = id;
        this.policyId = policyId;
        this.attestationId = attestationId;
        this.enclaveQuoteHash = enclaveQuoteHash;
        this.verificationStatus = verificationStatus;
        this.attestedAt = attestedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public void setPolicyId(UUID policyId) {
        this.policyId = policyId;
    }

    public String getAttestationId() {
        return attestationId;
    }

    public void setAttestationId(String attestationId) {
        this.attestationId = attestationId;
    }

    public String getEnclaveQuoteHash() {
        return enclaveQuoteHash;
    }

    public void setEnclaveQuoteHash(String enclaveQuoteHash) {
        this.enclaveQuoteHash = enclaveQuoteHash;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Instant getAttestedAt() {
        return attestedAt;
    }

    public void setAttestedAt(Instant attestedAt) {
        this.attestedAt = attestedAt;
    }
}
