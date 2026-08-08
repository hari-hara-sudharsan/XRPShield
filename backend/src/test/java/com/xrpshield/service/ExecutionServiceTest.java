package com.xrpshield.service;

import com.xrpshield.audit.ExecutionAuditService;
import com.xrpshield.dto.ExecutionResponseDto;
import com.xrpshield.dto.StartExecutionRequestDto;
import com.xrpshield.entity.*;
import com.xrpshield.gateway.ExecutionGateway;
import com.xrpshield.mapper.ExecutionMapper;
import com.xrpshield.repository.*;
import com.xrpshield.security.Role;
import com.xrpshield.validator.ExecutionValidator;
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

class ExecutionServiceTest {

    private TreasuryExecutionRepository executionRepository;
    private ExecutionHistoryRepository historyRepository;
    private ExecutionQueueRepository queueRepository;
    private ExecutionResultRepository resultRepository;
    private ExecutionMetadataRepository metadataRepository;
    private TreasuryDecisionRepository decisionRepository;
    private ExecutionAuditRepository auditRepository;

    private ExecutionGateway executionGateway;
    private ExecutionAuditService auditService;
    private ExecutionValidator validator;
    private ExecutionMapper mapper;
    private ExecutionService executionService;

    @BeforeEach
    void setUp() {
        executionRepository = Mockito.mock(TreasuryExecutionRepository.class);
        historyRepository = Mockito.mock(ExecutionHistoryRepository.class);
        queueRepository = Mockito.mock(ExecutionQueueRepository.class);
        resultRepository = Mockito.mock(ExecutionResultRepository.class);
        metadataRepository = Mockito.mock(ExecutionMetadataRepository.class);
        decisionRepository = Mockito.mock(TreasuryDecisionRepository.class);
        auditRepository = Mockito.mock(ExecutionAuditRepository.class);

        executionGateway = new ExecutionGateway();
        auditService = new ExecutionAuditService(auditRepository);
        validator = new ExecutionValidator();
        mapper = new ExecutionMapper();

        executionService = new ExecutionService(
                executionRepository, historyRepository, queueRepository,
                resultRepository, metadataRepository, decisionRepository,
                executionGateway, auditService, validator, mapper
        );
    }

    @Test
    @DisplayName("Should start protected execution for approved decision cleanly")
    void testStartExecution() {
        UserEntity user = new UserEntity("owner@xrpshield.io", "Owner Name", "hashedPass", Role.ROLE_USER, UserStatus.ACTIVE);
        user.setId(UUID.randomUUID());

        VaultEntity vault = new VaultEntity(user, "Primary FXRP Treasury", "0x123", "FXRP", BigDecimal.ZERO, VaultStatus.ACTIVE);
        vault.setId(UUID.randomUUID());

        TreasuryDecisionEntity decision = new TreasuryDecisionEntity(
                vault, null, "PROTECT_POSITION", 1, "APPROVED", "Rationale", "TEE-ATT-1", "0xHASH"
        );
        decision.setId(UUID.randomUUID());

        StartExecutionRequestDto request = new StartExecutionRequestDto();
        request.setDecisionId(decision.getId());

        when(decisionRepository.findById(decision.getId())).thenReturn(Optional.of(decision));
        when(executionRepository.findByDecisionId(decision.getId())).thenReturn(Optional.empty());
        when(executionRepository.save(any(TreasuryExecutionEntity.class))).thenAnswer(inv -> {
            TreasuryExecutionEntity e = inv.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        ExecutionResponseDto result = executionService.startExecution(request, user);

        assertNotNull(result);
        assertEquals("COMPLETED", result.getExecutionState());
        assertNotNull(result.getTxHash());
    }
}
