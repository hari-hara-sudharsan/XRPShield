package com.xrpshield.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CircuitBreakerService {

    public enum ExecutionState {
        READY,
        EVALUATION_PENDING,
        VERIFIED,
        EXECUTION_PENDING,
        EXECUTED,
        REJECTED,
        EXPIRED,
        FAILED,
        PAUSED
    }

    private int consecutiveFailures = 0;
    private boolean circuitBreakerActive = false;
    private ExecutionState currentState = ExecutionState.READY;

    public synchronized Map<String, Object> handleFailureScenario(String scenarioCode) {
        consecutiveFailures++;
        if (consecutiveFailures >= 3) {
            circuitBreakerActive = true;
            currentState = ExecutionState.PAUSED;
        } else {
            currentState = ExecutionState.FAILED;
        }

        String humanExplanation = getHumanExplanation(scenarioCode);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("scenarioCode", scenarioCode);
        response.put("currentState", currentState.name());
        response.put("consecutiveFailures", consecutiveFailures);
        response.put("circuitBreakerActive", circuitBreakerActive);
        response.put("userFacingExplanation", humanExplanation);
        response.put("hedgeExecuted", false);
        response.put("capitalSafe", true);
        return response;
    }

    public synchronized Map<String, Object> recordSuccess() {
        consecutiveFailures = 0;
        circuitBreakerActive = false;
        currentState = ExecutionState.EXECUTED;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("currentState", currentState.name());
        response.put("consecutiveFailures", 0);
        response.put("circuitBreakerActive", false);
        response.put("hedgeExecuted", true);
        return response;
    }

    public synchronized void resetCircuitBreaker() {
        consecutiveFailures = 0;
        circuitBreakerActive = false;
        currentState = ExecutionState.READY;
    }

    public boolean isCircuitBreakerActive() {
        return circuitBreakerActive;
    }

    public ExecutionState getCurrentState() {
        return currentState;
    }

    private String getHumanExplanation(String code) {
        switch (code.toUpperCase()) {
            case "FTSO_STALE": return "FTSO price feed is stale (>180s staleness threshold). Hedge execution blocked.";
            case "FCC_UNAVAILABLE": return "Flare Confidential Compute TEE extension offline. Hedge execution blocked.";
            case "RPC_UNAVAILABLE": return "Flare Coston2 RPC node unreachable. Execution halted for capital safety.";
            case "DEX_LIQUIDITY_INSUFFICIENT": return "SparkDEX FXRP/USDT0 liquidity insufficient. Trade rejected.";
            case "SLIPPAGE_EXCEEDED": return "DEX price impact exceeds maximum allowed 0.5% limit. Trade reverted.";
            case "QUOTE_EXPIRED": return "DEX quote expired (>60s staleness limit). Recalculate quote to proceed.";
            case "TEE_RESULT_EXPIRED": return "TEE attestation deadline expired. Request fresh policy evaluation.";
            case "POLICY_EXPIRED": return "Policy commitment deadline expired. Re-commit policy to execute.";
            case "USER_REJECTED": return "Web3 transaction signature rejected by user in MetaMask.";
            case "TRANSACTION_REVERTED": return "Smart contract execution reverted on Coston2 EVM. Balance unchanged.";
            default: return "External system failure detected. Execution halted safely without capital risk.";
        }
    }
}
