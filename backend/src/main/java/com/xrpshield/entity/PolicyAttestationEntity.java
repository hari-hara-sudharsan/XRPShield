package com.xrpshield.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "policy_attestations")
public class PolicyAttestationEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private ConfidentialPolicyEntity policy;

    @Column(name = "attestation_id", nullable = false, unique = true, length = 100)
    private String attestationId;

    @Column(name = "enclave_quote_hash", nullable = false, length = 66)
    private String enclaveQuoteHash;

    @Column(name = "verification_status", nullable = false, length = 20)
    private String verificationStatus = "VERIFIED";

    @Column(name = "attested_at", nullable = false)
    private Instant attestedAt = Instant.now();

    public PolicyAttestationEntity() {}

    public PolicyAttestationEntity(ConfidentialPolicyEntity policy, String attestationId, String enclaveQuoteHash, String verificationStatus) {
        this.policy = policy;
        this.attestationId = attestationId;
        this.enclaveQuoteHash = enclaveQuoteHash;
        this.verificationStatus = verificationStatus != null ? verificationStatus : "VERIFIED";
        this.attestedAt = Instant.now();
    }

    public ConfidentialPolicyEntity getPolicy() {
        return policy;
    }

    public void setPolicy(ConfidentialPolicyEntity policy) {
        this.policy = policy;
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
