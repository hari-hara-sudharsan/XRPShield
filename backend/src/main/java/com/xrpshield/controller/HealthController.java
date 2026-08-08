package com.xrpshield.controller;

import com.xrpshield.dto.ApiResponse;
import com.xrpshield.dto.HealthStatusResponse;
import com.xrpshield.service.SystemHealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Tag(name = "System Health", description = "Endpoints for verifying system health and sub-component operational status")
public class HealthController {

    private final SystemHealthService systemHealthService;

    @GetMapping
    @Operation(summary = "Get System Health Status", description = "Retrieves operational health details of XRPShield backend, database, and Flare network parameters")
    public ResponseEntity<ApiResponse<HealthStatusResponse>> getHealth() {
        HealthStatusResponse health = systemHealthService.getSystemHealth();
        return ResponseEntity.ok(ApiResponse.success("System health operational", health));
    }
}
