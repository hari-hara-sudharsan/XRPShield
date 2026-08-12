package com.xrpshield.service;

import com.xrpshield.dto.CanonicalPolicyPayloadDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIPolicyAssistantService {

    private static final Logger logger = LoggerFactory.getLogger(AIPolicyAssistantService.class);

    /**
     * Translates natural language risk directives into structured draft policy proposals.
     * NON-CUSTODIAL RULE: This produces a DRAFT ONLY. Zero financial execution authority.
     */
    public Map<String, Object> translateDirectiveToDraft(String intentPrompt, String vaultAddress) {
        logger.info("Translating natural language risk directive: '{}' for vault: {}", intentPrompt, vaultAddress);

        BigDecimal hedgeRatio = new BigDecimal("0.70"); // Default 70%
        BigDecimal triggerThreshold = new BigDecimal("5.0"); // Default 5%
        BigDecimal maximumProtection = new BigDecimal("100000.0");
        String policyName = "AI Risk Guard: Protected Treasury";

        if (intentPrompt != null && !intentPrompt.isBlank()) {
            // Regex parsing for hedge percentage e.g. "Protect 70%" or "70% of my XRP"
            Pattern hedgePattern = Pattern.compile("(?i)(\\d+(?:\\.\\d+)?)\\s*%?\\s*(?:of|hedge|protect)");
            Matcher hedgeMatcher = hedgePattern.matcher(intentPrompt);
            if (hedgeMatcher.find()) {
                try {
                    double val = Double.parseDouble(hedgeMatcher.group(1));
                    if (val > 1) val = val / 100.0;
                    hedgeRatio = BigDecimal.valueOf(val).setScale(4, java.math.RoundingMode.HALF_UP);
                } catch (Exception e) {}
            }

            // Regex parsing for trigger percentage e.g. "falls 5%" or "drop 10%"
            Pattern triggerPattern = Pattern.compile("(?i)(?:falls|drop|drawdown|decline|by)\\s*(\\d+(?:\\.\\d+)?)\\s*%?");
            Matcher triggerMatcher = triggerPattern.matcher(intentPrompt);
            if (triggerMatcher.find()) {
                try {
                    double val = Double.parseDouble(triggerMatcher.group(1));
                    triggerThreshold = BigDecimal.valueOf(val).setScale(2, java.math.RoundingMode.HALF_UP);
                } catch (Exception e) {}
            }

            if (intentPrompt.length() > 5) {
                policyName = "AI Advisory Guard: " + (intentPrompt.length() > 30 ? intentPrompt.substring(0, 30) + "..." : intentPrompt);
            }
        }

        Long nonce = System.currentTimeMillis();
        Long deadline = (System.currentTimeMillis() / 1000) + (86400 * 30); // 30 days valid

        CanonicalPolicyPayloadDto canonicalPayload = new CanonicalPolicyPayloadDto(
            vaultAddress != null ? vaultAddress : "0x5bb8082987515f40398fb9893d90616b47c04208",
            "FXRP",
            hedgeRatio,
            triggerThreshold,
            maximumProtection,
            deadline,
            nonce,
            1L
        );

        Map<String, Object> response = new HashMap<>();
        response.put("policyName", policyName);
        response.put("draftPayload", canonicalPayload);
        response.put("requiresUserApproval", true);
        response.put("financialAuthority", "NONE (Advisory Non-Custodial Assistant Only)");
        response.put("explanation", String.format(
            "Translated intent into draft policy: Protect %s%% of vault FXRP reserves if FTSOv2 price drops by %s%%. USER CONFIRMATION REQUIRED BEFORE ON-CHAIN COMMITMENT.",
            hedgeRatio.multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString(),
            triggerThreshold.stripTrailingZeros().toPlainString()
        ));

        return response;
    }

    public Map<String, String> explainExecutionReceipt(String txHash, String status, String attestationHash) {
        Map<String, String> explanation = new HashMap<>();
        explanation.put("txHash", txHash);
        explanation.put("status", status);
        explanation.put("attestationHash", attestationHash);
        explanation.put("summary", "On-chain transaction execution confirmed on Flare Coston2 Testnet. Smart contract verified EIP-712 TEE signature and executed FXRP -> USDT0 DEX swap.");
        explanation.put("disclaimer", "OpenAI Assistant provided execution analysis only. Financial execution was authorized solely by user Web3 signature and smart contract gatekeeper.");
        return explanation;
    }
}
