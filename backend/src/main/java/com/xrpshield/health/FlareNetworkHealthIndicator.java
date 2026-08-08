package com.xrpshield.health;

import com.xrpshield.blockchain.FlareNetworkConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlareNetworkHealthIndicator implements HealthIndicator {

    private final FlareNetworkConfig flareNetworkConfig;

    @Override
    public Health health() {
        if (flareNetworkConfig.getRpcUrl() != null && !flareNetworkConfig.getRpcUrl().isBlank()) {
            return Health.up()
                    .withDetail("flareRpcUrl", flareNetworkConfig.getRpcUrl())
                    .withDetail("chainId", flareNetworkConfig.getChainId())
                    .build();
        }
        return Health.down().withDetail("reason", "Flare RPC URL is not configured").build();
    }
}
