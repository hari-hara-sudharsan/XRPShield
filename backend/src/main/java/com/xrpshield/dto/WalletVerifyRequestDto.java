package com.xrpshield.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WalletVerifyRequestDto {

    @NotBlank(message = "Wallet address is required")
    @Size(min = 24, max = 64, message = "Invalid wallet address length")
    private String address;

    @NotBlank(message = "Signature is required")
    private String signature;

    @NotBlank(message = "Nonce is required")
    private String nonce;
    private String messageToSign;

    public WalletVerifyRequestDto() {}

    public WalletVerifyRequestDto(String address, String signature, String nonce) {
        this.address = address;
        this.signature = signature;
        this.nonce = nonce;
    }

    public WalletVerifyRequestDto(String address, String signature, String nonce, String messageToSign) {
        this.address = address;
        this.signature = signature;
        this.nonce = nonce;
        this.messageToSign = messageToSign;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getMessageToSign() {
        return messageToSign;
    }

    public void setMessageToSign(String messageToSign) {
        this.messageToSign = messageToSign;
    }
}

