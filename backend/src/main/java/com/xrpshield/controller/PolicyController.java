package com.xrpshield.controller;

import com.xrpshield.dto.*;
import com.xrpshield.entity.PolicyHistoryEntity;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.service.PolicyService;
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
@RequestMapping("/api/v1/policies")
@Tag(name = "Confidential Policy Engine", description = "Endpoints for Flare Confidential Compute (FCC) policy definition, AES-256 encryption, and TEE attestation verification")
@SecurityRequirement(name = "bearerAuth")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping
    @Operation(summary = "Create Confidential Policy", description = "Defines a new encrypted risk policy and submits it to Flare Confidential Compute (FCC)")
    public ResponseEntity<ApiResponse<PolicyResponseDto>> createPolicy(
            @Valid @RequestBody CreatePolicyRequestDto request,
            @AuthenticationPrincipal UserEntity currentUser) {

        PolicyResponseDto policy = policyService.createPolicy(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Confidential policy created and attested successfully", policy));
    }

    @GetMapping
    @Operation(summary = "List User Policies", description = "Retrieves all confidential policies defined for the authenticated user's vaults")
    public ResponseEntity<ApiResponse<List<PolicyResponseDto>>> getAllPolicies(
            @AuthenticationPrincipal UserEntity currentUser) {

        List<PolicyResponseDto> policies = policyService.getAllPolicies(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Policies retrieved successfully", policies));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Policy Details", description = "Retrieves public metadata and status for a specific confidential policy")
    public ResponseEntity<ApiResponse<PolicyResponseDto>> getPolicyById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserEntity currentUser) {

        PolicyResponseDto policy = policyService.getPolicyById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Policy details retrieved successfully", policy));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Policy Version", description = "Updates confidential policy parameters and increments version")
    public ResponseEntity<ApiResponse<PolicyResponseDto>> updatePolicy(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePolicyRequestDto request,
            @AuthenticationPrincipal UserEntity currentUser) {

        PolicyResponseDto policy = policyService.updatePolicy(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Policy updated successfully", policy));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate Policy", description = "Marks a confidential policy as DEACTIVATED")
    public ResponseEntity<ApiResponse<Void>> deletePolicy(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserEntity currentUser) {

        policyService.deletePolicy(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Policy deactivated successfully", null));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get Policy History", description = "Retrieves audit and version history for a policy")
    public ResponseEntity<ApiResponse<List<PolicyHistoryEntity>>> getPolicyHistory(@PathVariable UUID id) {
        List<PolicyHistoryEntity> history = policyService.getPolicyHistory(id);
        return ResponseEntity.ok(ApiResponse.success("Policy history retrieved successfully", history));
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Get Policy Evaluation Status", description = "Retrieves latest TEE enclave evaluation result for a policy")
    public ResponseEntity<ApiResponse<PolicyEvaluationResponseDto>> getPolicyStatus(@PathVariable UUID id) {
        PolicyEvaluationResponseDto status = policyService.getPolicyStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Policy evaluation status retrieved successfully", status));
    }

    @GetMapping("/{id}/attestation")
    @Operation(summary = "Get TEE Attestation Proof", description = "Retrieves Flare Confidential Compute (FCC) cryptographic quote and attestation proof")
    public ResponseEntity<ApiResponse<PolicyAttestationResponseDto>> getPolicyAttestation(@PathVariable UUID id) {
        PolicyAttestationResponseDto attestation = policyService.getPolicyAttestation(id);
        return ResponseEntity.ok(ApiResponse.success("TEE attestation proof retrieved successfully", attestation));
    }
}
