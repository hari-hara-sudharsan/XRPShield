package com.xrpshield.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public class VaultWithdrawalRequestDto {

    @NotNull(message = "Vault ID is required")
    private UUID vaultId;

    @NotNull(message = "Withdrawal amount is required")
    @Positive(message = "Withdrawal amount must be positive")
    private BigDecimal amount;

    private String currency = "FXRP";

    private String recipientAddress;

    public UUID getVaultId() {
        return vaultId;
    }

    public void setVaultId(UUID vaultId) {
        this.vaultId = vaultId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }
}
