package com.xrpshield.scheduler;

import com.xrpshield.integration.HealthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HealthMonitorScheduler {

    private static final Logger logger = LoggerFactory.getLogger(HealthMonitorScheduler.class);

    private final HealthService healthService;

    public HealthMonitorScheduler(HealthService healthService) {
        this.healthService = healthService;
    }

    @Scheduled(fixedRate = 30000) // Health monitor every 30 seconds
    public void monitorSubsystemHealth() {
        try {
            Map<String, Object> health = healthService.getAggregatedHealth();
            String status = (String) health.get("status");
            logger.info("SCHEDULED_HEALTH_CHECK | Application Operational Status: {}", status);
        } catch (Exception e) {
            logger.error("Scheduled health check failed: {}", e.getMessage());
        }
    }
}
