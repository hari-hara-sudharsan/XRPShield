package com.xrpshield.controller;

import com.xrpshield.dto.ApiResponse;
import com.xrpshield.dto.AuthResponseDto;
import com.xrpshield.dto.WalletNonceRequestDto;
import com.xrpshield.dto.WalletNonceResponseDto;
import com.xrpshield.dto.WalletResponseDto;
import com.xrpshield.dto.WalletVerifyRequestDto;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.service.WalletAuthService;
import com.xrpshield.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet")
@Tag(name = "Wallet Authentication & Web3 Management", description = "Endpoints for MetaMask connection, cryptographic nonce generation, EIP-191 signature verification, and wallet linkage")
public class WalletController {

    private final WalletAuthService walletAuthService;
    private final WalletService walletService;

    public WalletController(WalletAuthService walletAuthService, WalletService walletService) {
        this.walletAuthService = walletAuthService;
        this.walletService = walletService;
    }

    @PostMapping("/nonce")
    @Operation(summary = "Generate Cryptographic Nonce", description = "Generates a unique authentication nonce and challenge string for a Web3 wallet address")
    public ResponseEntity<ApiResponse<WalletNonceResponseDto>> getNonce(@Valid @RequestBody WalletNonceRequestDto request) {
        WalletNonceResponseDto response = walletAuthService.generateNonce(request.getAddress());
        return ResponseEntity.ok(ApiResponse.success("Authentication nonce generated", response));
    }

    @PostMapping("/connect")
    @Operation(summary = "Connect Wallet", description = "Prepares wallet authentication flow for MetaMask client")
    public ResponseEntity<ApiResponse<WalletNonceResponseDto>> connect(@Valid @RequestBody WalletNonceRequestDto request) {
        WalletNonceResponseDto response = walletAuthService.generateNonce(request.getAddress());
        return ResponseEntity.ok(ApiResponse.success("Wallet connection initialized", response));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify Signature & Login", description = "Verifies EIP-191 personal signature from MetaMask against nonce and issues JWT tokens")
    public ResponseEntity<ApiResponse<AuthResponseDto>> verifySignature(@Valid @RequestBody WalletVerifyRequestDto request) {
        AuthResponseDto response = walletAuthService.verifySignatureAndLogin(request);
        return ResponseEntity.ok(ApiResponse.success("Wallet signature verified", response));
    }

    @PostMapping("/disconnect")
    @Operation(summary = "Disconnect Wallet", description = "Terminates wallet session")
    public ResponseEntity<ApiResponse<String>> disconnect() {
        return ResponseEntity.ok(ApiResponse.success("Wallet disconnected successfully", "Session cleared"));
    }

    @GetMapping("/status")
    @Operation(summary = "Get Linked Wallet Status", description = "Retrieves all linked wallets for the authenticated user")
    public ResponseEntity<ApiResponse<List<WalletResponseDto>>> getWalletStatus(@AuthenticationPrincipal UserEntity user) {
        List<WalletResponseDto> wallets = walletService.getWalletsByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Linked wallet status retrieved", wallets));
    }
}
