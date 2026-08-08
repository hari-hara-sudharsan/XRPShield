package com.xrpshield.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WalletNonceRequestDto {

    @NotBlank(message = "Wallet address is required")
    @Size(min = 24, max = 64, message = "Invalid wallet address length")
    private String address;

    public WalletNonceRequestDto() {}

    public WalletNonceRequestDto(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
