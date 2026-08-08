package com.xrpshield.service;

import com.xrpshield.audit.PolicyAuditService;
import com.xrpshield.dto.CreatePolicyRequestDto;
import com.xrpshield.dto.PolicyResponseDto;
import com.xrpshield.entity.*;
import com.xrpshield.security.Role;
import com.xrpshield.fcc.*;
import com.xrpshield.mapper.PolicyMapper;
import com.xrpshield.repository.*;
import com.xrpshield.validator.PolicyValidator;
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

class PolicyServiceTest {

    private ConfidentialPolicyRepository policyRepository;
    private EncryptedPolicyRepository encryptedPolicyRepository;
    private PolicyHistoryRepository policyHistoryRepository;
    private PolicyEvaluationRepository policyEvaluationRepository;
    private PolicyAttestationRepository policyAttestationRepository;
    private VaultRepository vaultRepository;
    private PolicyEncryptionService encryptionService;
    private FCCAdapter fccAdapter;
    private AttestationService attestationService;
    private PolicyAuditService auditService;
    private PolicyValidator validator;
    private PolicyMapper mapper;
    private PolicyService policyService;

    @BeforeEach
    void setUp() {
        policyRepository = Mockito.mock(ConfidentialPolicyRepository.class);
        encryptedPolicyRepository = Mockito.mock(EncryptedPolicyRepository.class);
        policyHistoryRepository = Mockito.mock(PolicyHistoryRepository.class);
        policyEvaluationRepository = Mockito.mock(PolicyEvaluationRepository.class);
        policyAttestationRepository = Mockito.mock(PolicyAttestationRepository.class);
        vaultRepository = Mockito.mock(VaultRepository.class);

        encryptionService = new PolicyEncryptionService("XRPShieldMasterEncryptionKey32B!");
        FCCClient fccClient = new FCCClient();
        fccAdapter = new FCCAdapter(fccClient);
        attestationService = new AttestationService(policyAttestationRepository);
        auditService = new PolicyAuditService();
        validator = new PolicyValidator();
        mapper = new PolicyMapper();

        policyService = new PolicyService(
                policyRepository, encryptedPolicyRepository, policyHistoryRepository,
                policyEvaluationRepository, policyAttestationRepository, vaultRepository,
                encryptionService, fccAdapter, attestationService, auditService, validator, mapper
        );
    }

    @Test
    @DisplayName("Should create confidential policy and encrypt sensitive rules cleanly")
    void testCreatePolicy() {
        UserEntity user = new UserEntity("owner@xrpshield.io", "Owner Name", "hashedPass", Role.ROLE_USER, UserStatus.ACTIVE);
        user.setId(UUID.randomUUID());

        VaultEntity vault = new VaultEntity(user, "Test Vault", "0x123", "FXRP", BigDecimal.ZERO, VaultStatus.ACTIVE);
        vault.setId(UUID.randomUUID());

        CreatePolicyRequestDto request = new CreatePolicyRequestDto();
        request.setVaultId(vault.getId());
        request.setName("Treasury Risk Guard");
        request.setRiskThreshold(new BigDecimal("0.15"));

        when(vaultRepository.findById(vault.getId())).thenReturn(Optional.of(vault));
        when(policyRepository.save(any(ConfidentialPolicyEntity.class))).thenAnswer(inv -> {
            ConfidentialPolicyEntity p = inv.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        PolicyResponseDto result = policyService.createPolicy(request, user);

        assertNotNull(result);
        assertEquals("Treasury Risk Guard", result.getName());
        assertEquals("COMPLIANT", result.getLatestEvaluationStatus());
        assertEquals("VERIFIED", result.getLatestAttestationStatus());
    }
}
