package com.xrpshield.controller;

import com.xrpshield.dto.ApiResponse;
import com.xrpshield.service.AIPolicyAssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "Non-Custodial AI Policy Assistant", description = "Endpoints for translating natural language risk directives into structured draft policies (Zero Financial Authority)")
public class AIPolicyAssistantController {

    private final AIPolicyAssistantService aiAssistantService;

    public AIPolicyAssistantController(AIPolicyAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping("/translate-policy")
    @Operation(summary = "Translate Intent to Draft Policy", description = "Translates natural language risk directives into a structured policy proposal. REQUIRES USER CONFIRMATION.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> translatePolicy(
            @RequestBody Map<String, String> request) {

        String intent = request.get("intent");
        String vaultAddress = request.get("vaultAddress");

        Map<String, Object> draft = aiAssistantService.translateDirectiveToDraft(intent, vaultAddress);
        return ResponseEntity.ok(ApiResponse.success("AI Policy draft generated. USER CONFIRMATION REQUIRED BEFORE ON-CHAIN SUBMISSION.", draft));
    }

    @PostMapping("/explain-execution")
    @Operation(summary = "Explain On-Chain Execution", description = "Generates human-readable explanation of on-chain execution receipt")
    public ResponseEntity<ApiResponse<Map<String, String>>> explainExecution(
            @RequestBody Map<String, String> request) {

        String txHash = request.get("txHash");
        String status = request.get("status");
        String attestationHash = request.get("attestationHash");

        Map<String, String> explanation = aiAssistantService.explainExecutionReceipt(txHash, status, attestationHash);
        return ResponseEntity.ok(ApiResponse.success("On-chain execution explanation generated", explanation));
    }
}
