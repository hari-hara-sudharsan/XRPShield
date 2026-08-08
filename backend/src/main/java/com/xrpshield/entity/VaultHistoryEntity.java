package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vault_history")
public class VaultHistoryEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vault_id", nullable = false)
    private VaultEntity vault;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "actor_address", nullable = false, length = 64)
    private String actorAddress;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    public VaultHistoryEntity() {}

    public VaultHistoryEntity(VaultEntity vault, String action, String actorAddress, String details) {
        this.vault = vault;
        this.action = action;
        this.actorAddress = actorAddress;
        this.details = details;
    }

    public VaultEntity getVault() {
        return vault;
    }

    public void setVault(VaultEntity vault) {
        this.vault = vault;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActorAddress() {
        return actorAddress;
    }

    public void setActorAddress(String actorAddress) {
        this.actorAddress = actorAddress;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
