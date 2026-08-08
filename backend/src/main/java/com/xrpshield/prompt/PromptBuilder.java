package com.xrpshield.prompt;

import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    public String buildSanitizedPrompt(String rawPrompt) {
        if (rawPrompt == null) return "";
        // Strip private keys, raw TEE payloads, and sensitive tokens
        String cleaned = rawPrompt.replaceAll("(?i)private[_-]?key[\\s:=]+[a-zA-Z0-9]+", "[REDACTED]")
                .replaceAll("(?i)0x[a-fA-F0-9]{64}", "[REDACTED_HASH]");
        return cleaned.trim();
    }

    public String buildPolicyDraftPrompt(String userIntent, String vaultName) {
        return "System Instruction: You are the XRPShield AI Policy Assistant. Create a draft treasury risk management policy matching the user requirement.\n" +
                "Vault Target: " + (vaultName != null ? vaultName : "General Treasury Vault") + "\n" +
                "User Intent: " + buildSanitizedPrompt(userIntent) + "\n" +
                "Strict Constraints: Return structured JSON containing policyName, maxDrawdownPercent, minLiquidityThreshold, triggerCondition, and rationale. Do NOT generate trading advice.";
    }

    public String buildDecisionExplanationPrompt(String decisionType, String rationale, String attestationId) {
        return "System Instruction: Explain this XRPShield treasury decision to the vault manager in clear, plain business language.\n" +
                "Decision Type: " + decisionType + "\n" +
                "Policy Rationale: " + rationale + "\n" +
                "Flare TEE Attestation ID: " + attestationId + "\n" +
                "Strict Constraints: Explain public conditions met. Do NOT expose confidential enclave memory or raw key material.";
    }

    public String buildReportPrompt(String vaultName, String reportType, String metricsSummary) {
        return "System Instruction: Generate a formal executive report for XRPShield Treasury Management.\n" +
                "Vault: " + vaultName + "\n" +
                "Report Type: " + reportType + "\n" +
                "Metrics Summary: " + metricsSummary + "\n" +
                "Strict Constraints: Provide executive summary, key risks, execution status, and recommendations.";
    }
}
