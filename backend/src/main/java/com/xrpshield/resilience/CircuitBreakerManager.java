package com.xrpshield.resilience;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CircuitBreakerManager {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerManager.class);

    private final Map<String, String> circuitStates = new ConcurrentHashMap<>();

    public CircuitBreakerManager() {
        circuitStates.put("BLOCKCHAIN_RPC", "CLOSED");
        circuitStates.put("FCC_ENCLAVE", "CLOSED");
        circuitStates.put("OPENAI_API", "CLOSED");
        circuitStates.put("DATABASE", "CLOSED");
    }

    public String getState(String serviceName) {
        return circuitStates.getOrDefault(serviceName, "CLOSED");
    }

    public void recordFailure(String serviceName) {
        logger.warn("Circuit Breaker failure recorded for service: {}", serviceName);
    }

    public void recordSuccess(String serviceName) {
        circuitStates.put(serviceName, "CLOSED");
    }
}
