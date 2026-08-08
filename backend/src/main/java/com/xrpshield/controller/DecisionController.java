package com.xrpshield.controller;

import com.xrpshield.dto.*;
import com.xrpshield.entity.DecisionHistoryEntity;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.service.DecisionHistoryService;
import com.xrpshield.service.DecisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/decision")
@Tag(name = "Treasury Decision Engine", description = "Endpoints for intelligent treasury policy decision generation, queuing, and attestation reporting")
@SecurityRequirement(name = "bearerAuth")
public class DecisionController {

    private final DecisionService decisionService;
    private final DecisionHistoryService historyService;

    public DecisionController(DecisionService decisionService, DecisionHistoryService historyService) {
        this.decisionService = decisionService;
        this.historyService = historyService;
    }

    @PostMapping("/evaluate")
    @Operation(summary = "Evaluate Treasury Decision", description = "Evaluates vault policies via Flare Confidential Compute (FCC) enclaves and generates a versioned decision")
    public ResponseEntity<ApiResponse<DecisionResponseDto>> evaluateDecision(
            @Valid @RequestBody EvaluateDecisionRequestDto request,
            @AuthenticationPrincipal UserEntity currentUser) {

        DecisionResponseDto decision = decisionService.evaluateDecision(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Treasury decision generated successfully", decision));
    }

    @GetMapping
    @Operation(summary = "List User Decisions", description = "Retrieves all decisions generated for the authenticated user's vaults")
    public ResponseEntity<ApiResponse<List<DecisionResponseDto>>> getUserDecisions(
            @AuthenticationPrincipal UserEntity currentUser) {

        List<DecisionResponseDto> decisions = decisionService.getUserDecisions(currentUser);
        return ResponseEntity.ok(ApiResponse.success("User decisions retrieved successfully", decisions));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Decision Details", description = "Retrieves specific decision details, rationale, and attestation ID")
    public ResponseEntity<ApiResponse<DecisionResponseDto>> getDecisionById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserEntity currentUser) {

        DecisionResponseDto decision = decisionService.getDecisionById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Decision details retrieved successfully", decision));
    }

    @GetMapping("/history")
    @Operation(summary = "Get Decision Version History", description = "Retrieves audit and version history for a specific decision")
    public ResponseEntity<ApiResponse<List<DecisionHistoryEntity>>> getDecisionHistory(@RequestParam UUID decisionId) {
        List<DecisionHistoryEntity> history = historyService.getHistory(decisionId);
        return ResponseEntity.ok(ApiResponse.success("Decision history retrieved successfully", history));
    }

    @GetMapping("/queue")
    @Operation(summary = "Get Active Decision Queue", description = "Retrieves pending and processing items in the decision queue")
    public ResponseEntity<ApiResponse<List<DecisionQueueResponseDto>>> getActiveQueue(
            @AuthenticationPrincipal UserEntity currentUser) {

        List<DecisionQueueResponseDto> queue = decisionService.getActiveQueue(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Active decision queue retrieved successfully", queue));
    }

    @GetMapping("/status")
    @Operation(summary = "Get Decision Status & Latency Metrics", description = "Retrieves monitoring metrics including decision latency, FCC latency, and queue size")
    public ResponseEntity<ApiResponse<DecisionStatusMetricsDto>> getStatusMetrics(
            @AuthenticationPrincipal UserEntity currentUser) {

        DecisionStatusMetricsDto metrics = decisionService.getMetrics(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Status metrics retrieved successfully", metrics));
    }
}
