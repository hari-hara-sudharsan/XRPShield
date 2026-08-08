package com.xrpshield.controller;

import com.xrpshield.dto.ApiResponse;
import com.xrpshield.dto.SystemInfoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/info")
@Tag(name = "System Information", description = "Endpoints exposing build details, environment properties, and system metadata")
public class SystemInfoController {

    @Value("${spring.application.name:xrpshield-backend}")
    private String applicationName;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @GetMapping
    @Operation(summary = "Get Application & Build Metadata", description = "Retrieves build information, active profiles, Java version, and system timestamp")
    public ResponseEntity<ApiResponse<SystemInfoResponseDto>> getSystemInfo() {
        SystemInfoResponseDto info = new SystemInfoResponseDto(
                applicationName,
                "1.0.0",
                activeProfile,
                System.getProperty("java.version"),
                activeProfile,
                Instant.now(),
                Map.of(
                        "buildTool", "Maven 3.9+",
                        "javaTarget", "21 LTS",
                        "database", "Supabase PostgreSQL",
                        "confidentialCompute", "Flare TEE Enclave Ready"
                )
        );

        return ResponseEntity.ok(ApiResponse.success("System information retrieved successfully", info));
    }
}
