package com.xrpshield.controller;

import com.xrpshield.dto.ApiResponse;
import com.xrpshield.integration.HealthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SystemControllerTest {

    private SystemController systemController;

    @BeforeEach
    void setUp() {
        HealthService healthService = new HealthService(null, null, null, null) {
            @Override
            public Map<String, Object> getAggregatedHealth() {
                Map<String, Object> map = new HashMap<>();
                map.put("status", "UP");
                return map;
            }
        };

        systemController = new SystemController(healthService, null, null);
    }

    @Test
    @DisplayName("Should return 200 OK and aggregated system status")
    void testGetSystemStatus() {
        ResponseEntity<ApiResponse<Map<String, Object>>> response = systemController.getSystemStatus();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("UP", response.getBody().getData().get("status"));
    }
}

