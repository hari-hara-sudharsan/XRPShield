package com.xrpshield.monitoring;

import com.xrpshield.dto.PlatformMetricsDto;
import org.springframework.stereotype.Component;

@Component
public class PlatformMetricsCollector {

    public PlatformMetricsDto collectSystemMetrics() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemoryMb = (totalMemory - freeMemory) / (1024 * 1024);

        return new PlatformMetricsDto(
                usedMemoryMb, 12L, 99.8, 45L, 85L, 1200L, 0L
        );
    }
}
