package com.xrpshield.service;

import com.xrpshield.dto.HealthStatusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class SystemHealthService {

    @Value("${spring.application.name:xrpshield-backend}")
    private String applicationName;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Value("${xrpshield.flare.chain-id:114}")
    private String flareChainId;

    public HealthStatusResponse getSystemHealth() {
        Map<String, Object> components = new HashMap<>();
        components.put("database", "CONFIGURED");
        components.put("flareNetwork", Map.of("chainId", flareChainId, "status", "UP"));
        components.put("confidentialCompute", "READY");

        return new HealthStatusResponse(
                "UP",
                applicationName,
                "1.0.0",
                activeProfile,
                Instant.now(),
                components
        );
    }
}
