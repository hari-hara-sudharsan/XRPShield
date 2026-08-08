package com.xrpshield.integration;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class HealthService {

    private final DatabaseGateway databaseGateway;
    private final BlockchainGateway blockchainGateway;
    private final WalletGateway walletGateway;
    private final AuthenticationGateway authenticationGateway;

    public HealthService(DatabaseGateway databaseGateway, BlockchainGateway blockchainGateway, WalletGateway walletGateway, AuthenticationGateway authenticationGateway) {
        this.databaseGateway = databaseGateway;
        this.blockchainGateway = blockchainGateway;
        this.walletGateway = walletGateway;
        this.authenticationGateway = authenticationGateway;
    }

    public Map<String, Object> getAggregatedHealth() {
        Map<String, Object> health = new HashMap<>();

        Map<String, Object> db = databaseGateway.getDatabaseSummary();
        Map<String, Object> bc = blockchainGateway.getBlockchainSummary();

        boolean isDbHealthy = "CONNECTED".equals(db.get("status"));
        boolean isBcHealthy = Boolean.TRUE.equals(bc.get("isConnected"));

        health.put("status", isDbHealthy && isBcHealthy ? "UP" : "DEGRADED");
        health.put("timestamp", Instant.now());
        health.put("database", db);
        health.put("blockchain", bc);
        health.put("walletAuth", walletGateway.getWalletSummary());
        health.put("authentication", authenticationGateway.getAuthenticationSummary());

        return health;
    }
}
