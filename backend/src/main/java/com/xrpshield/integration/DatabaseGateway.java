package com.xrpshield.integration;

import com.xrpshield.repository.UserRepository;
import com.xrpshield.repository.VaultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DatabaseGateway {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseGateway.class);

    private final UserRepository userRepository;
    private final VaultRepository vaultRepository;

    public DatabaseGateway(UserRepository userRepository, VaultRepository vaultRepository) {
        this.userRepository = userRepository;
        this.vaultRepository = vaultRepository;
    }

    public Map<String, Object> getDatabaseSummary() {
        Map<String, Object> summary = new HashMap<>();

        long startTime = System.currentTimeMillis();
        long userCount = userRepository.count();
        long dbLatency = System.currentTimeMillis() - startTime;

        summary.put("status", "CONNECTED");
        summary.put("provider", "Supabase PostgreSQL");
        summary.put("latencyMs", dbLatency);
        summary.put("usersRegistered", userCount);
        summary.put("vaultsRegistered", vaultRepository.count());
        logger.debug("Database summary queried successfully");

        return summary;

    }
}
