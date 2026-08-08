package com.xrpshield.service;

import com.xrpshield.dto.AIResponseDto;
import com.xrpshield.dto.ExplainDecisionRequestDto;
import com.xrpshield.dto.GenerateDraftPolicyRequestDto;
import com.xrpshield.entity.*;
import com.xrpshield.prompt.AIResponseParser;
import com.xrpshield.prompt.PromptBuilder;
import com.xrpshield.repository.*;
import com.xrpshield.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PolicyAssistantServiceTest {

    private PolicyDraftRepository policyDraftRepository;
    private GeneratedReportRepository generatedReportRepository;
    private UserPreferenceRepository userPreferenceRepository;
    private TreasuryDecisionRepository decisionRepository;
    private VaultRepository vaultRepository;
    private AIConversationRepository conversationRepository;
    private PromptHistoryRepository promptHistoryRepository;

    private ConversationService conversationService;
    private AIValidationService validationService;
    private PromptBuilder promptBuilder;
    private AIResponseParser responseParser;
    private PolicyAssistantService policyAssistantService;

    @BeforeEach
    void setUp() {
        policyDraftRepository = Mockito.mock(PolicyDraftRepository.class);
        generatedReportRepository = Mockito.mock(GeneratedReportRepository.class);
        userPreferenceRepository = Mockito.mock(UserPreferenceRepository.class);
        decisionRepository = Mockito.mock(TreasuryDecisionRepository.class);
        vaultRepository = Mockito.mock(VaultRepository.class);
        conversationRepository = Mockito.mock(AIConversationRepository.class);
        promptHistoryRepository = Mockito.mock(PromptHistoryRepository.class);

        conversationService = new ConversationService(conversationRepository, promptHistoryRepository);
        validationService = new AIValidationService();
        promptBuilder = new PromptBuilder();
        responseParser = new AIResponseParser();

        policyAssistantService = new PolicyAssistantService(
                policyDraftRepository, generatedReportRepository, userPreferenceRepository,
                decisionRepository, vaultRepository, conversationService,
                validationService, promptBuilder, responseParser
        );
    }

    @Test
    @DisplayName("Should generate draft treasury policy cleanly")
    void testGenerateDraftPolicy() {
        UserEntity user = new UserEntity("owner@xrpshield.io", "Owner Name", "hashedPass", Role.ROLE_USER, UserStatus.ACTIVE);
        user.setId(UUID.randomUUID());

        GenerateDraftPolicyRequestDto request = new GenerateDraftPolicyRequestDto();
        request.setIntent("Protect vault when drawdown exceeds 10%");

        when(policyDraftRepository.save(any(PolicyDraftEntity.class))).thenAnswer(inv -> {
            PolicyDraftEntity p = inv.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        AIResponseDto result = policyAssistantService.generateDraftPolicy(request, user);

        assertNotNull(result);
        assertEquals("POLICY_DRAFT", result.getResponseType());
        assertTrue(result.getContent().contains("maxDrawdownPercent"));
    }

    @Test
    @DisplayName("Should explain treasury decision in plain language")
    void testExplainDecision() {
        UserEntity user = new UserEntity("owner@xrpshield.io", "Owner Name", "hashedPass", Role.ROLE_USER, UserStatus.ACTIVE);
        user.setId(UUID.randomUUID());

        VaultEntity vault = new VaultEntity(user, "Primary FXRP Treasury", "0x123", "FXRP", BigDecimal.ZERO, VaultStatus.ACTIVE);
        vault.setId(UUID.randomUUID());

        TreasuryDecisionEntity decision = new TreasuryDecisionEntity(
                vault, null, "PROTECT_POSITION", 1, "APPROVED", "Rationale", "FCC-ATT-123", "0xHASH"
        );
        decision.setId(UUID.randomUUID());

        ExplainDecisionRequestDto request = new ExplainDecisionRequestDto();
        request.setDecisionId(decision.getId());

        when(decisionRepository.findById(decision.getId())).thenReturn(Optional.of(decision));

        AIResponseDto result = policyAssistantService.explainDecision(request, user);

        assertNotNull(result);
        assertEquals("DECISION_EXPLANATION", result.getResponseType());
        assertTrue(result.getContent().contains("PROTECT_POSITION"));
    }
}
