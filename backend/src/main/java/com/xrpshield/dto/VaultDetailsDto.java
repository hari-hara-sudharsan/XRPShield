package com.xrpshield.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class VaultDetailsDto {

    private UUID id;
    private String name;
    private String description;
    private String address;
    private String status;
    private String ownerEmail;
    private BigDecimal balanceAmount;
    private BigDecimal lockedAmount;
    private String currency;
    private Instant createdAt;

    public VaultDetailsDto() {}

    public VaultDetailsDto(UUID id, String name, String description, String address, String status, String ownerEmail, BigDecimal balanceAmount, BigDecimal lockedAmount, String currency, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.status = status;
        this.ownerEmail = ownerEmail;
        this.balanceAmount = balanceAmount;
        this.lockedAmount = lockedAmount;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public BigDecimal getLockedAmount() {
        return lockedAmount;
    }

    public void setLockedAmount(BigDecimal lockedAmount) {
        this.lockedAmount = lockedAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
