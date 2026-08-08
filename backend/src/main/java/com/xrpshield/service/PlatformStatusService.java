package com.xrpshield.service;

import com.xrpshield.dto.*;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.monitoring.PlatformHealthIndicator;
import com.xrpshield.monitoring.PlatformMetricsCollector;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlatformStatusService {

    private final PlatformHealthIndicator healthIndicator;
    private final PlatformMetricsCollector metricsCollector;
    private final AlertNotificationService notificationService;

    public PlatformStatusService(PlatformHealthIndicator healthIndicator, PlatformMetricsCollector metricsCollector, AlertNotificationService notificationService) {
        this.healthIndicator = healthIndicator;
        this.metricsCollector = metricsCollector;
        this.notificationService = notificationService;
    }

    public PlatformStatusDto getPlatformStatus() {
        List<ComponentHealthDto> components = healthIndicator.checkAllComponents();
        boolean allUp = components.stream().allMatch(c -> "UP".equalsIgnoreCase(c.getStatus()));
        return new PlatformStatusDto(allUp ? "HEALTHY" : "DEGRADED", Instant.now(), components);
    }

    public PlatformMetricsDto getPlatformMetrics() {
        return metricsCollector.collectSystemMetrics();
    }

    public List<ComponentHealthDto> getHealthDetails() {
        return healthIndicator.checkAllComponents();
    }

    public List<PlatformNotificationDto> getUserNotifications(UserEntity user) {
        return notificationService.getUserNotifications(user).stream()
                .map(n -> new PlatformNotificationDto(
                        n.getId(), n.getSeverity(), n.getTitle(), n.getMessage(), n.getIsRead(), n.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
