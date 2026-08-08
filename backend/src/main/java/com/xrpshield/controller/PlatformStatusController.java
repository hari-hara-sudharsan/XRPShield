package com.xrpshield.controller;

import com.xrpshield.dto.*;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.service.PlatformStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/platform")
@Tag(name = "Platform Status & Monitoring", description = "Endpoints for enterprise system health checks, operational metrics, circuit breakers, and platform notifications")
@SecurityRequirement(name = "bearerAuth")
public class PlatformStatusController {

    private final PlatformStatusService platformStatusService;

    public PlatformStatusController(PlatformStatusService platformStatusService) {
        this.platformStatusService = platformStatusService;
    }

    @GetMapping("/status")
    @Operation(summary = "Get Platform Status", description = "Retrieves high-level operational status of all XRPShield platform subsystems")
    public ResponseEntity<ApiResponse<PlatformStatusDto>> getPlatformStatus() {
        PlatformStatusDto status = platformStatusService.getPlatformStatus();
        return ResponseEntity.ok(ApiResponse.success("Platform status retrieved successfully", status));
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get System Metrics", description = "Retrieves operational metrics including JVM memory, DB pool, RPC latency, and TEE enclave speed")
    public ResponseEntity<ApiResponse<PlatformMetricsDto>> getPlatformMetrics() {
        PlatformMetricsDto metrics = platformStatusService.getPlatformMetrics();
        return ResponseEntity.ok(ApiResponse.success("Platform metrics retrieved successfully", metrics));
    }

    @GetMapping("/health")
    @Operation(summary = "Get Subsystem Health Matrix", description = "Retrieves individual status and latency metrics for DB, RPC, FCC, AI, and Wallet services")
    public ResponseEntity<ApiResponse<List<ComponentHealthDto>>> getComponentHealth() {
        List<ComponentHealthDto> health = platformStatusService.getHealthDetails();
        return ResponseEntity.ok(ApiResponse.success("Subsystem health matrix retrieved successfully", health));
    }

    @GetMapping("/notifications")
    @Operation(summary = "Get User Notifications", description = "Retrieves severity-categorized platform alert notifications for the user")
    public ResponseEntity<ApiResponse<List<PlatformNotificationDto>>> getNotifications(
            @AuthenticationPrincipal UserEntity currentUser) {

        List<PlatformNotificationDto> notifications = platformStatusService.getUserNotifications(currentUser);
        return ResponseEntity.ok(ApiResponse.success("User notifications retrieved successfully", notifications));
    }
}
