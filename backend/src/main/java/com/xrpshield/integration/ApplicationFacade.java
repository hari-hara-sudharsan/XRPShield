package com.xrpshield.integration;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApplicationFacade {

    private final HealthService healthService;
    private final ConfigurationService configurationService;
    private final IntegrationService integrationService;

    public ApplicationFacade(HealthService healthService, ConfigurationService configurationService, IntegrationService integrationService) {
        this.healthService = healthService;
        this.configurationService = configurationService;
        this.integrationService = integrationService;
    }

    public Map<String, Object> getCompleteSystemState() {
        Map<String, Object> state = new HashMap<>();
        state.put("health", healthService.getAggregatedHealth());
        state.put("featureFlags", configurationService.getFeatureFlags());
        state.put("modules", integrationService.getModuleStatusMap());
        return state;
    }
}
