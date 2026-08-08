package com.xrpshield.dto;

public class WalletNonceResponseDto {

    private String address;
    private String nonce;
    private String messageToSign;

    public WalletNonceResponseDto() {}

    public WalletNonceResponseDto(String address, String nonce, String messageToSign) {
        this.address = address;
        this.nonce = nonce;
        this.messageToSign = messageToSign;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
