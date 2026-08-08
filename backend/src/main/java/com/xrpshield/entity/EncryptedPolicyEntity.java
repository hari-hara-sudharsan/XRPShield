package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "encrypted_policies")
public class EncryptedPolicyEntity extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false, unique = true)
    private ConfidentialPolicyEntity policy;

    @Column(name = "encrypted_payload", nullable = false, columnDefinition = "TEXT")
    private String encryptedPayload;

    @Column(name = "iv", nullable = false, length = 64)
    private String iv;

    @Column(name = "auth_tag", nullable = false, length = 64)
    private String authTag;

    @Column(name = "key_id", nullable = false, length = 100)
    private String keyId = "SYSTEM_MASTER_KMS_V1";

    public EncryptedPolicyEntity() {}

    public EncryptedPolicyEntity(ConfidentialPolicyEntity policy, String encryptedPayload, String iv, String authTag, String keyId) {
        this.policy = policy;
        this.encryptedPayload = encryptedPayload;
        this.iv = iv;
        this.authTag = authTag;
        this.keyId = keyId != null ? keyId : "SYSTEM_MASTER_KMS_V1";
    }

    public ConfidentialPolicyEntity getPolicy() {
        return policy;
    }

    public void setPolicy(ConfidentialPolicyEntity policy) {
        this.policy = policy;
    }

    public String getEncryptedPayload() {
        return encryptedPayload;
    }

    public void setEncryptedPayload(String encryptedPayload) {
        this.encryptedPayload = encryptedPayload;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getAuthTag() {
        return authTag;
    }

    public void setAuthTag(String authTag) {
        this.authTag = authTag;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
}
