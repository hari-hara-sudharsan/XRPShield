package com.xrpshield.controller;

import com.xrpshield.dto.ApiResponse;
import com.xrpshield.integration.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/application")
@Tag(name = "Application Lifecycle & Architecture", description = "Endpoints exposing overall application information and architecture metadata")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/info")
    @Operation(summary = "Get Unified Application Information", description = "Retrieves application metadata, architectural layer description, and complete system state")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getApplicationInfo() {
        Map<String, Object> info = applicationService.getApplicationInformation();
        return ResponseEntity.ok(ApiResponse.success("Application information retrieved", info));
    }
}
