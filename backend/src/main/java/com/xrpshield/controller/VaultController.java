package com.xrpshield.controller;

import com.xrpshield.dto.*;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.service.VaultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vault")
@Tag(name = "Confidential Vault Infrastructure", description = "Endpoints for confidential treasury vault creation, balance tracking, FXRP deposits, and withdrawals")
@SecurityRequirement(name = "bearerAuth")
public class VaultController {

    private final VaultService vaultService;

    public VaultController(VaultService vaultService) {
        this.vaultService = vaultService;
    }

    @PostMapping
    @Operation(summary = "Create Confidential Treasury Vault", description = "Registers a new FXRP/XRP treasury vault for the authenticated user")
    public ResponseEntity<ApiResponse<VaultDetailsDto>> createVault(
            @Valid @RequestBody CreateVaultRequestDto request,
            @AuthenticationPrincipal UserEntity currentUser) {

        VaultDetailsDto vault = vaultService.createVault(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Confidential vault created successfully", vault));
    }

    @GetMapping
    @Operation(summary = "List User Treasury Vaults", description = "Retrieves all vaults owned by the authenticated user")
    public ResponseEntity<ApiResponse<List<VaultDetailsDto>>> getUserVaults(
            @AuthenticationPrincipal UserEntity currentUser) {

        List<VaultDetailsDto> vaults = vaultService.getUserVaults(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("User vaults retrieved successfully", vaults));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Vault Details", description = "Retrieves specific vault metadata, address, and current FXRP balance")
    public ResponseEntity<ApiResponse<VaultDetailsDto>> getVaultById(@PathVariable UUID id) {
        VaultDetailsDto vault = vaultService.getVaultDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Vault details retrieved successfully", vault));
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit FXRP into Vault", description = "Records and processes FXRP deposit transaction into specified vault")
    public ResponseEntity<ApiResponse<VaultTransactionResponseDto>> deposit(
            @Valid @RequestBody VaultDepositRequestDto request,
            @AuthenticationPrincipal UserEntity currentUser) {

        VaultTransactionResponseDto tx = vaultService.deposit(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Deposit processed successfully", tx));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw FXRP from Vault", description = "Processes FXRP withdrawal transaction from specified vault")
    public ResponseEntity<ApiResponse<VaultTransactionResponseDto>> withdraw(
            @Valid @RequestBody VaultWithdrawalRequestDto request,
            @AuthenticationPrincipal UserEntity currentUser) {

        VaultTransactionResponseDto tx = vaultService.withdraw(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Withdrawal processed successfully", tx));
    }

    @GetMapping("/history")
    @Operation(summary = "Get Vault Transaction History", description = "Retrieves on-chain deposit and withdrawal transaction logs for a vault")
    public ResponseEntity<ApiResponse<List<VaultTransactionResponseDto>>> getVaultHistory(
            @RequestParam UUID vaultId) {

        List<VaultTransactionResponseDto> history = vaultService.getVaultHistory(vaultId);
        return ResponseEntity.ok(ApiResponse.success("Vault history retrieved successfully", history));
    }
}
