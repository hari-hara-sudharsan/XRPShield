package com.xrpshield.controller;

import com.xrpshield.dto.*;
import com.xrpshield.entity.AIConversationEntity;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.service.ConversationService;
import com.xrpshield.service.PolicyAssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "Treasury Intelligence Layer", description = "Endpoints for AI Policy Assistant, Decision Explanations, Vault Insights, and Executive Report Generation")
@SecurityRequirement(name = "bearerAuth")
public class PolicyAssistantController {

    private final PolicyAssistantService policyAssistantService;
    private final ConversationService conversationService;
    private final com.xrpshield.repository.UserRepository userRepository;

    public PolicyAssistantController(PolicyAssistantService policyAssistantService, ConversationService conversationService, com.xrpshield.repository.UserRepository userRepository) {
        this.policyAssistantService = policyAssistantService;
        this.conversationService = conversationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/policy")
    @Operation(summary = "Generate Draft Policy", description = "Translates natural language user intent into structured treasury risk policy parameters")
    public ResponseEntity<ApiResponse<AIResponseDto>> generateDraftPolicy(
            @Valid @RequestBody GenerateDraftPolicyRequestDto request,
            @AuthenticationPrincipal UserEntity currentUser) {

        UserEntity user = getEffectiveUser(currentUser);
        AIResponseDto response = policyAssistantService.generateDraftPolicy(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Draft policy generated successfully", response));
    }

    @PostMapping("/explain")
    @Operation(summary = "Explain Decision", description = "Generates plain-language explanation of why a treasury decision occurred based on public policy bounds")
    public ResponseEntity<ApiResponse<AIResponseDto>> explainDecision(
            @Valid @RequestBody ExplainDecisionRequestDto request,
            @AuthenticationPrincipal UserEntity currentUser) {

        UserEntity user = getEffectiveUser(currentUser);
        AIResponseDto response = policyAssistantService.explainDecision(request, user);
        return ResponseEntity.ok(ApiResponse.success("Decision explanation generated successfully", response));
    }

    @PostMapping("/report")
    @Operation(summary = "Generate Vault Executive Report", description = "Generates executive report summarizing vault status, active policies, and execution audit metrics")
    public ResponseEntity<ApiResponse<AIResponseDto>> generateReport(
            @Valid @RequestBody GenerateReportRequestDto request,
            @AuthenticationPrincipal UserEntity currentUser) {

        UserEntity user = getEffectiveUser(currentUser);
        AIResponseDto response = policyAssistantService.generateReport(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Executive report generated successfully", response));
    }

    private UserEntity getEffectiveUser(UserEntity currentUser) {
        if (currentUser != null) return currentUser;
        return userRepository.findByEmail("owner@xrpshield.io")
                .orElseGet(() -> userRepository.save(new UserEntity(null, "Demo Treasury Owner", "owner@xrpshield.io", com.xrpshield.security.Role.ROLE_USER, com.xrpshield.entity.UserStatus.ACTIVE)));
    }



    @GetMapping("/history")
    @Operation(summary = "Get AI Conversation History", description = "Retrieves stored AI conversations and prompt histories for the user")
    public ResponseEntity<ApiResponse<List<AIConversationEntity>>> getHistory(
            @AuthenticationPrincipal UserEntity currentUser) {

        List<AIConversationEntity> history = conversationService.getUserConversations(currentUser);
        return ResponseEntity.ok(ApiResponse.success("AI conversation history retrieved successfully", history));
    }

    @GetMapping("/preferences")
    @Operation(summary = "Get AI Preferences", description = "Retrieves user's AI model and explanation verbosity preferences")
    public ResponseEntity<ApiResponse<UserPreferenceDto>> getPreferences(
            @AuthenticationPrincipal UserEntity currentUser) {

        UserPreferenceDto preferences = policyAssistantService.getUserPreferences(currentUser);
        return ResponseEntity.ok(ApiResponse.success("AI preferences retrieved successfully", preferences));
    }
}
