package com.xrpshield.service;

import com.xrpshield.audit.PolicyAuditService;
import com.xrpshield.dto.*;
import com.xrpshield.entity.*;
import com.xrpshield.exception.BusinessException;
import com.xrpshield.exception.ResourceNotFoundException;
import com.xrpshield.fcc.AttestationService;
import com.xrpshield.fcc.FCCAdapter;
import com.xrpshield.fcc.FCCClient;
import com.xrpshield.fcc.PolicyEncryptionService;
import com.xrpshield.mapper.PolicyMapper;
import com.xrpshield.repository.*;
import com.xrpshield.validator.PolicyValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyService.class);

    private final ConfidentialPolicyRepository policyRepository;
    private final EncryptedPolicyRepository encryptedPolicyRepository;
    private final PolicyHistoryRepository policyHistoryRepository;
    private final PolicyEvaluationRepository policyEvaluationRepository;
    private final PolicyAttestationRepository policyAttestationRepository;
    private final VaultRepository vaultRepository;
    private final PolicyEncryptionService encryptionService;
    private final FCCAdapter fccAdapter;
    private final AttestationService attestationService;
    private final PolicyAuditService auditService;
    private final PolicyValidator validator;
    private final PolicyMapper mapper;

    public PolicyService(ConfidentialPolicyRepository policyRepository, EncryptedPolicyRepository encryptedPolicyRepository,
                         PolicyHistoryRepository policyHistoryRepository, PolicyEvaluationRepository policyEvaluationRepository,
                         PolicyAttestationRepository policyAttestationRepository, VaultRepository vaultRepository,
                         PolicyEncryptionService encryptionService, FCCAdapter fccAdapter, AttestationService attestationService,
                         PolicyAuditService auditService, PolicyValidator validator, PolicyMapper mapper) {
        this.policyRepository = policyRepository;
        this.encryptedPolicyRepository = encryptedPolicyRepository;
        this.policyHistoryRepository = policyHistoryRepository;
        this.policyEvaluationRepository = policyEvaluationRepository;
        this.policyAttestationRepository = policyAttestationRepository;
        this.vaultRepository = vaultRepository;
        this.encryptionService = encryptionService;
        this.fccAdapter = fccAdapter;
        this.attestationService = attestationService;
        this.auditService = auditService;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Transactional
    public PolicyResponseDto createPolicy(CreatePolicyRequestDto dto, UserEntity user) {
        logger.debug("Creating policy: {} for user: {}", dto.getName(), user.getEmail());
        validator.validatePolicyCreation(dto);


        VaultEntity vault = vaultRepository.findById(dto.getVaultId())
                .orElseThrow(() -> new ResourceNotFoundException("Vault", "id", dto.getVaultId()));

        if (!vault.getOwner().getId().equals(user.getId())) {
            throw new BusinessException("Unauthorized: You do not own this vault");
        }

        String sensitiveRulesJson = String.format(
                "{\"riskThreshold\":%s,\"maxExposure\":%s,\"maxDrawdown\":%s,\"maxPositionSize\":%s,\"stopCondition\":\"%s\",\"emergencyExit\":\"%s\"}",
                dto.getRiskThreshold(), dto.getMaxExposure(), dto.getMaxDrawdown(),
                dto.getMaxPositionSize(), dto.getStopCondition(), dto.getEmergencyExit()
        );

        String policyHash = computeSha256(sensitiveRulesJson);

        ConfidentialPolicyEntity policy = new ConfidentialPolicyEntity(
                vault, dto.getName(), 1, "ACTIVE",
                dto.getPublicMetadata() != null ? dto.getPublicMetadata() : "Asset: " + dto.getAsset(),
                policyHash
        );
        ConfidentialPolicyEntity savedPolicy = policyRepository.save(policy);

        PolicyEncryptionService.EncryptedPayload enc = encryptionService.encrypt(sensitiveRulesJson);
        EncryptedPolicyEntity encryptedEntity = new EncryptedPolicyEntity(
                savedPolicy, enc.getEncryptedPayload(), enc.getIv(), enc.getAuthTag(), "SYSTEM_MASTER_KMS_V1"
        );
        encryptedPolicyRepository.save(encryptedEntity);

        PolicyHistoryEntity history = new PolicyHistoryEntity(savedPolicy, 1, "Initial policy creation");
        policyHistoryRepository.save(history);

        FCCClient.FCCExecutionResult fccResult = fccAdapter.submitToEnclave(savedPolicy, encryptedEntity);

        PolicyEvaluationEntity eval = new PolicyEvaluationEntity(savedPolicy, vault, fccResult.getStatus(), fccResult.getSummary());
        policyEvaluationRepository.save(eval);

        attestationService.recordAttestation(savedPolicy, fccResult.getAttestationId(), fccResult.getEnclaveQuoteHash());

        auditService.logPolicyEvent(savedPolicy, "POLICY_CREATED_AND_ATTESTED", user.getEmail(), "Created policy with hash: " + policyHash);

        return mapper.toDto(savedPolicy, fccResult.getStatus(), "VERIFIED");
    }

    public List<PolicyResponseDto> getAllPolicies(UserEntity user) {
        return policyRepository.findAll().stream()
                .filter(p -> p.getVault().getOwner().getId().equals(user.getId()))
                .map(p -> mapper.toDto(p, "COMPLIANT", "VERIFIED"))
                .collect(Collectors.toList());
    }

    public PolicyResponseDto getPolicyById(UUID id, UserEntity user) {
        ConfidentialPolicyEntity policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", "id", id));

        if (!policy.getVault().getOwner().getId().equals(user.getId())) {
            throw new BusinessException("Unauthorized access to policy");
        }

        return mapper.toDto(policy, "COMPLIANT", "VERIFIED");
    }

    @Transactional
    public PolicyResponseDto updatePolicy(UUID id, UpdatePolicyRequestDto dto, UserEntity user) {
        ConfidentialPolicyEntity policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", "id", id));

        if (!policy.getVault().getOwner().getId().equals(user.getId())) {
            throw new BusinessException("Unauthorized access to policy");
        }

        policy.setPolicyName(dto.getName());
        policy.setPolicyVersion(policy.getPolicyVersion() + 1);
        policy.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");

        ConfidentialPolicyEntity saved = policyRepository.save(policy);

        PolicyHistoryEntity history = new PolicyHistoryEntity(saved, saved.getPolicyVersion(), "Updated policy metadata to version " + saved.getPolicyVersion());
        policyHistoryRepository.save(history);

        auditService.logPolicyEvent(saved, "POLICY_UPDATED", user.getEmail(), "Updated to version: " + saved.getPolicyVersion());

        return mapper.toDto(saved, "COMPLIANT", "VERIFIED");
    }

    @Transactional
    public void deletePolicy(UUID id, UserEntity user) {
        ConfidentialPolicyEntity policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", "id", id));

        if (!policy.getVault().getOwner().getId().equals(user.getId())) {
            throw new BusinessException("Unauthorized access to policy");
        }

        policy.setStatus("DEACTIVATED");
        policyRepository.save(policy);
        auditService.logPolicyEvent(policy, "POLICY_DEACTIVATED", user.getEmail(), "Policy marked DEACTIVATED");
    }

    public List<PolicyHistoryEntity> getPolicyHistory(UUID policyId) {
        return policyHistoryRepository.findByPolicyIdOrderByPolicyVersionDesc(policyId);
    }

    public PolicyEvaluationResponseDto getPolicyStatus(UUID policyId) {
        PolicyEvaluationEntity eval = policyEvaluationRepository.findTopByPolicyIdOrderByEvaluatedAtDesc(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("PolicyEvaluation", "policyId", policyId));

        return new PolicyEvaluationResponseDto(
                eval.getId(), eval.getPolicy().getId(), eval.getVault().getId(),
                eval.getEvaluationStatus(), eval.getResultSummary(), eval.getEvaluatedAt()
        );
    }

    public PolicyAttestationResponseDto getPolicyAttestation(UUID policyId) {
        PolicyAttestationEntity att = policyAttestationRepository.findTopByPolicyIdOrderByAttestedAtDesc(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("PolicyAttestation", "policyId", policyId));

        return new PolicyAttestationResponseDto(
                att.getId(), att.getPolicy().getId(), att.getAttestationId(),
                att.getEnclaveQuoteHash(), att.getVerificationStatus(), att.getAttestedAt()
        );
    }

    private String computeSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return "0x" + HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            return "0x" + UUID.randomUUID().toString().replace("-", "");
        }
    }
}
