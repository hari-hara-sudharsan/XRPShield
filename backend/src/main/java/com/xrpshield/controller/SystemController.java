package com.xrpshield.controller;

import com.xrpshield.dto.ApiResponse;
import com.xrpshield.integration.ConfigurationService;
import com.xrpshield.integration.HealthService;
import com.xrpshield.integration.IntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "System Aggregation & Infrastructure Status", description = "Endpoints exposing aggregated status across Database, Blockchain RPC, Web3 Wallet Auth, Smart Contracts, and Feature Flags")
public class SystemController {

    private final HealthService healthService;
    private final ConfigurationService configurationService;
    private final IntegrationService integrationService;

    @Value("${spring.application.name:xrpshield-backend}")
    private String applicationName;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    public SystemController(HealthService healthService, ConfigurationService configurationService, IntegrationService integrationService) {
        this.healthService = healthService;
        this.configurationService = configurationService;
        this.integrationService = integrationService;
    }

    @GetMapping("/status")
    @Operation(summary = "Get Aggregated System Status", description = "Aggregates operational status from Database, Flare RPC, Wallet Auth, and Contracts")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStatus() {
        Map<String, Object> health = healthService.getAggregatedHealth();
        return ResponseEntity.ok(ApiResponse.success("System status aggregated", health));
    }

    @GetMapping("/health")
    @Operation(summary = "Subsystem Health Monitoring", description = "Retrieves DB & RPC latency measurements and health state")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemHealth() {
        Map<String, Object> health = healthService.getAggregatedHealth();
        return ResponseEntity.ok(ApiResponse.success("Subsystem health operational", health));
    }

    @GetMapping("/version")
    @Operation(summary = "Get System Version & Profile", description = "Returns build version, active Spring profile, and JDK runtime info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemVersion() {
        Map<String, Object> ver = new HashMap<>();
        ver.put("applicationName", applicationName);
        ver.put("version", "1.0.0");
        ver.put("activeProfile", activeProfile);
        ver.put("javaVersion", System.getProperty("java.version"));
        ver.put("serverTime", Instant.now());

        return ResponseEntity.ok(ApiResponse.success("System version retrieved", ver));
    }

    @GetMapping("/configuration")
    @Operation(summary = "Get Application Configuration & Feature Flags", description = "Retrieves active feature toggles and global settings")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> getSystemConfiguration() {
        Map<String, Boolean> flags = configurationService.getFeatureFlags();
        return ResponseEntity.ok(ApiResponse.success("Feature flags retrieved", flags));
    }

    @GetMapping("/modules")
    @Operation(summary = "Get Subsystem Modules Status", description = "Retrieves operational status of every registered application subsystem module")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemModules() {
        Map<String, Object> modules = integrationService.getModuleStatusMap();
        return ResponseEntity.ok(ApiResponse.success("Subsystem module status retrieved", modules));
    }
}
