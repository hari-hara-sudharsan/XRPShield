package com.xrpshield.controller;

import com.xrpshield.dto.*;
import com.xrpshield.entity.ExecutionHistoryEntity;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.service.ExecutionService;
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
@RequestMapping("/api/v1/execution")
@Tag(name = "Protected Treasury Execution Engine", description = "Endpoints for executing approved confidential decisions on-chain, retry management, and receipt monitoring")
@SecurityRequirement(name = "bearerAuth")
public class ExecutionController {

    private final ExecutionService executionService;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping("/start")
    @Operation(summary = "Start Decision Execution", description = "Submits an approved confidential decision to the protected on-chain execution engine")
    public ResponseEntity<ApiResponse<ExecutionResponseDto>> startExecution(
            @Valid @RequestBody StartExecutionRequestDto request,
            @AuthenticationPrincipal UserEntity currentUser) {

        ExecutionResponseDto execution = executionService.startExecution(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Protected execution started successfully", execution));
    }

    @PostMapping("/cancel")
    @Operation(summary = "Cancel Execution", description = "Cancels a pending or queued execution request")
    public ResponseEntity<ApiResponse<ExecutionResponseDto>> cancelExecution(
            @RequestParam UUID executionId,
            @AuthenticationPrincipal UserEntity currentUser) {

        ExecutionResponseDto execution = executionService.cancelExecution(executionId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Execution cancelled successfully", execution));
    }

    @GetMapping
    @Operation(summary = "List User Executions", description = "Retrieves all execution records for the authenticated user's vaults")
    public ResponseEntity<ApiResponse<List<ExecutionResponseDto>>> getUserExecutions(
            @AuthenticationPrincipal UserEntity currentUser) {

        List<ExecutionResponseDto> executions = executionService.getUserExecutions(currentUser);
        return ResponseEntity.ok(ApiResponse.success("User executions retrieved successfully", executions));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Execution Details", description = "Retrieves specific execution metadata, transaction hash, block number, and status")
    public ResponseEntity<ApiResponse<ExecutionResponseDto>> getExecutionById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserEntity currentUser) {

        ExecutionResponseDto execution = executionService.getExecutionById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Execution details retrieved successfully", execution));
    }

    @GetMapping("/history")
    @Operation(summary = "Get Execution History", description = "Retrieves audit and state transition history for an execution")
    public ResponseEntity<ApiResponse<List<ExecutionHistoryEntity>>> getExecutionHistory(@RequestParam UUID executionId) {
        List<ExecutionHistoryEntity> history = executionService.getExecutionHistory(executionId);
        return ResponseEntity.ok(ApiResponse.success("Execution history retrieved successfully", history));
    }

    @GetMapping("/queue")
    @Operation(summary = "Get Active Execution Queue", description = "Retrieves queued and processing execution items")
    public ResponseEntity<ApiResponse<List<ExecutionQueueResponseDto>>> getActiveQueue(
            @AuthenticationPrincipal UserEntity currentUser) {

        List<ExecutionQueueResponseDto> queue = executionService.getActiveQueue(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Active execution queue retrieved successfully", queue));
    }

    @GetMapping("/status")
    @Operation(summary = "Get Execution Status & Metrics", description = "Retrieves execution success rate, confirmation latency, and queue size metrics")
    public ResponseEntity<ApiResponse<ExecutionStatusMetricsDto>> getStatusMetrics(
            @AuthenticationPrincipal UserEntity currentUser) {

        ExecutionStatusMetricsDto metrics = executionService.getMetrics(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Execution status metrics retrieved successfully", metrics));
    }
}
