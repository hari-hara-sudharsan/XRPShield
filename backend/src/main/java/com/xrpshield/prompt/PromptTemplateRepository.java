package com.xrpshield.prompt;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PromptTemplateRepository {

    private final Map<String, String> templates = new ConcurrentHashMap<>();

    public PromptTemplateRepository() {
        templates.put("POLICY_DRAFT", "Draft a privacy-preserving XRP treasury policy for vault {vaultName} with intent: {intent}");
        templates.put("DECISION_EXPLANATION", "Explain why decision {decisionType} occurred for vault {vaultName} based on rationale: {rationale}");
        templates.put("VAULT_REPORT", "Generate executive treasury report for vault {vaultName} covering type: {reportType}");
    }

    public String getTemplate(String key) {
        return templates.getOrDefault(key, "Provide standard XRPShield AI assistance for: {intent}");
    }
}
