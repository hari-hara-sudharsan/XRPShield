package com.xrpshield.service;

import com.xrpshield.audit.DecisionAuditService;
import com.xrpshield.dto.DecisionResponseDto;
import com.xrpshield.dto.EvaluateDecisionRequestDto;
import com.xrpshield.entity.*;
import com.xrpshield.fcc.FCCClient;
import com.xrpshield.mapper.DecisionMapper;
import com.xrpshield.repository.*;
import com.xrpshield.security.Role;
import com.xrpshield.validator.DecisionValidator;
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

class DecisionServiceTest {

    private TreasuryDecisionRepository decisionRepository;
    private DecisionEvaluationRepository decisionEvaluationRepository;
    private DecisionQueueRepository decisionQueueRepository;
    private DecisionMetadataRepository decisionMetadataRepository;
    private VaultRepository vaultRepository;
    private ConfidentialPolicyRepository policyRepository;
    private DecisionHistoryRepository decisionHistoryRepository;
    private DecisionAuditRepository decisionAuditRepository;

    private FCCClient fccClient;
    private DecisionHistoryService historyService;
    private DecisionAuditService auditService;
    private DecisionValidator validator;
    private DecisionMapper mapper;
    private DecisionService decisionService;

    @BeforeEach
    void setUp() {
        decisionRepository = Mockito.mock(TreasuryDecisionRepository.class);
        decisionEvaluationRepository = Mockito.mock(DecisionEvaluationRepository.class);
        decisionQueueRepository = Mockito.mock(DecisionQueueRepository.class);
        decisionMetadataRepository = Mockito.mock(DecisionMetadataRepository.class);
        vaultRepository = Mockito.mock(VaultRepository.class);
        policyRepository = Mockito.mock(ConfidentialPolicyRepository.class);
        decisionHistoryRepository = Mockito.mock(DecisionHistoryRepository.class);
        decisionAuditRepository = Mockito.mock(DecisionAuditRepository.class);

        fccClient = new FCCClient();
        historyService = new DecisionHistoryService(decisionHistoryRepository);
        auditService = new DecisionAuditService(decisionAuditRepository);
        validator = new DecisionValidator();
        mapper = new DecisionMapper();

        decisionService = new DecisionService(
                decisionRepository, decisionEvaluationRepository, decisionQueueRepository,
                decisionMetadataRepository, vaultRepository, policyRepository,
                fccClient, null, historyService, auditService, validator, mapper
        );
    }

    @Test
    @DisplayName("Should evaluate confidential vault policy and generate versioned decision cleanly")
    void testEvaluateDecision() {
        UserEntity user = new UserEntity("owner@xrpshield.io", "Owner Name", "hashedPass", Role.ROLE_USER, UserStatus.ACTIVE);
        user.setId(UUID.randomUUID());

        VaultEntity vault = new VaultEntity(user, "Primary FXRP Treasury", "0x123", "FXRP", BigDecimal.ZERO, VaultStatus.ACTIVE);
        vault.setId(UUID.randomUUID());

        EvaluateDecisionRequestDto request = new EvaluateDecisionRequestDto();
        request.setVaultId(vault.getId());
        request.setPreferredDecisionType("PROTECT_POSITION");

        when(vaultRepository.findById(vault.getId())).thenReturn(Optional.of(vault));
        when(decisionRepository.save(any(TreasuryDecisionEntity.class))).thenAnswer(inv -> {
            TreasuryDecisionEntity d = inv.getArgument(0);
            d.setId(UUID.randomUUID());
            return d;
        });

        DecisionResponseDto result = decisionService.evaluateDecision(request, user);

        assertNotNull(result);
        assertEquals("PROTECT_POSITION", result.getDecisionType());
        assertEquals("PENDING", result.getStatus());
        assertEquals(1, result.getVersion());
    }
}
