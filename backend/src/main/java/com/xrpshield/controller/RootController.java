package com.xrpshield.controller;

import com.xrpshield.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRootInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "XRPShield REST API Backend");
        info.put("version", "1.0.0");
        info.put("status", "UP");
        info.put("frontendUrl", "http://localhost:3000");
        info.put("healthEndpoint", "/api/v1/health");
        info.put("swaggerUi", "/swagger-ui.html");
        info.put("timestamp", Instant.now());
        return ResponseEntity.ok(ApiResponse.success("XRPShield Backend REST API Operational", info));
    }
}
