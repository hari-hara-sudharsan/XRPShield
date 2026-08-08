package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "policy_drafts")
public class PolicyDraftEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vault_id")
    private VaultEntity vault;

    @Column(name = "draft_name", nullable = false, length = 150)
    private String draftName;

    @Column(name = "draft_json", nullable = false, columnDefinition = "TEXT")
    private String draftJson;

    public PolicyDraftEntity() {}

    public PolicyDraftEntity(UserEntity user, VaultEntity vault, String draftName, String draftJson) {
        this.user = user;
        this.vault = vault;
        this.draftName = draftName;
        this.draftJson = draftJson;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public VaultEntity getVault() {
        return vault;
    }

    public void setVault(VaultEntity vault) {
        this.vault = vault;
    }

    public String getDraftName() {
        return draftName;
    }

    public void setDraftName(String draftName) {
        this.draftName = draftName;
    }

    public String getDraftJson() {
        return draftJson;
    }

    public void setDraftJson(String draftJson) {
        this.draftJson = draftJson;
    }
}
