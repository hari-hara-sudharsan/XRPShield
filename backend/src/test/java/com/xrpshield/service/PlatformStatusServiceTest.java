package com.xrpshield.service;

import com.xrpshield.dto.PlatformMetricsDto;
import com.xrpshield.dto.PlatformStatusDto;
import com.xrpshield.monitoring.PlatformHealthIndicator;
import com.xrpshield.monitoring.PlatformMetricsCollector;
import com.xrpshield.repository.PlatformNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;


class PlatformStatusServiceTest {

    private PlatformNotificationRepository notificationRepository;
    private PlatformHealthIndicator healthIndicator;
    private PlatformMetricsCollector metricsCollector;
    private AlertNotificationService notificationService;
    private PlatformStatusService platformStatusService;

    @BeforeEach
    void setUp() {
        notificationRepository = Mockito.mock(PlatformNotificationRepository.class);
        healthIndicator = new PlatformHealthIndicator();
        metricsCollector = new PlatformMetricsCollector();
        notificationService = new AlertNotificationService(notificationRepository);

        platformStatusService = new PlatformStatusService(
                healthIndicator, metricsCollector, notificationService
        );
    }

    @Test
    @DisplayName("Should report platform status as HEALTHY when all components UP")
    void testGetPlatformStatus() {
        PlatformStatusDto status = platformStatusService.getPlatformStatus();

        assertNotNull(status);
        assertEquals("HEALTHY", status.getOverallStatus());
        assertFalse(status.getComponents().isEmpty());
    }

    @Test
    @DisplayName("Should collect system metrics cleanly")
    void testGetPlatformMetrics() {
        PlatformMetricsDto metrics = platformStatusService.getPlatformMetrics();

        assertNotNull(metrics);
        assertTrue(metrics.getUsedMemoryMb() >= 0);
        assertEquals(99.8, metrics.getSystemUptimePercentage());
    }
}
