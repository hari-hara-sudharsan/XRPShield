package com.xrpshield.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class OpenAISafetyService {

    private static final List<String> FORBIDDEN_PATTERNS = Arrays.asList(
            "override", "execute max", "send funds", "bypass", "ignore policy",
            "sign transaction", "synthetic hash", "unlimited slippage", "transfer all"
    );

    public Map<String, Object> processUserPrompt(String prompt) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (prompt == null || prompt.isBlank()) {
            response.put("success", false);
            response.put("error", "Empty prompt submitted.");
            return response;
        }

        String lower = prompt.toLowerCase();
        for (String pattern : FORBIDDEN_PATTERNS) {
            if (lower.contains(pattern)) {
                response.put("success", false);
                response.put("status", "REJECTED_SAFETY_BOUNDARY");
                response.put("error", "Security Policy Violation: Prompt injection attempt detected ('" + pattern + "'). OpenAI LLM has ZERO execution authority.");
                response.put("isAdvisoryOnly", true);
                return response;
            }
        }

        // Return structured advisory proposal
        response.put("success", true);
        response.put("status", "ADVISORY_PROPOSAL_GENERATED");
        response.put("isAdvisoryOnly", true);
        response.put("proposedPolicy", Map.of(
                "triggerThresholdPercent", -15.00,
                "hedgeRatioPercent", 100.00,
                "maxHedgeAmountFXRP", 10000,
                "maxSlippagePercent", 0.50
        ));
        response.put("disclaimer", "This proposal is purely advisory. User manual Web3 wallet signature and Flare TEE attestation are strictly required for on-chain execution.");
        return response;
    }
}
