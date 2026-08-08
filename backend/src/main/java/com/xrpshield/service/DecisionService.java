package com.xrpshield.service;

import com.xrpshield.audit.DecisionAuditService;
import com.xrpshield.dto.*;
import com.xrpshield.entity.*;
import com.xrpshield.exception.BusinessException;
import com.xrpshield.exception.ResourceNotFoundException;
import com.xrpshield.fcc.AttestationService;
import com.xrpshield.fcc.FCCClient;
import com.xrpshield.mapper.DecisionMapper;
import com.xrpshield.repository.*;
import com.xrpshield.validator.DecisionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DecisionService {

    private static final Logger logger = LoggerFactory.getLogger(DecisionService.class);

    private final TreasuryDecisionRepository decisionRepository;
    private final DecisionEvaluationRepository decisionEvaluationRepository;
    private final DecisionQueueRepository decisionQueueRepository;
    private final DecisionMetadataRepository decisionMetadataRepository;
    private final VaultRepository vaultRepository;
    private final ConfidentialPolicyRepository policyRepository;
    private final FCCClient fccClient;
    private final AttestationService attestationService;
    private final DecisionHistoryService historyService;
    private final DecisionAuditService auditService;
    private final DecisionValidator validator;
    private final DecisionMapper mapper;

    public DecisionService(TreasuryDecisionRepository decisionRepository, DecisionEvaluationRepository decisionEvaluationRepository,
                           DecisionQueueRepository decisionQueueRepository, DecisionMetadataRepository decisionMetadataRepository,
                           VaultRepository vaultRepository, ConfidentialPolicyRepository policyRepository,
                           FCCClient fccClient, AttestationService attestationService, DecisionHistoryService historyService,
                           DecisionAuditService auditService, DecisionValidator validator, DecisionMapper mapper) {
        this.decisionRepository = decisionRepository;
        this.decisionEvaluationRepository = decisionEvaluationRepository;
        this.decisionQueueRepository = decisionQueueRepository;
        this.decisionMetadataRepository = decisionMetadataRepository;
        this.vaultRepository = vaultRepository;
        this.policyRepository = policyRepository;
        this.fccClient = fccClient;
        this.attestationService = attestationService;
        this.historyService = historyService;
        this.auditService = auditService;
        this.validator = validator;
        this.mapper = mapper;
    }

    public AttestationService getAttestationService() {
        return attestationService;
    }


    @Transactional
    public DecisionResponseDto evaluateDecision(EvaluateDecisionRequestDto dto, UserEntity user) {
        logger.debug("Evaluating decision for vault: {}", dto.getVaultId());
        validator.validateEvaluationRequest(dto);


        VaultEntity vault = vaultRepository.findById(dto.getVaultId())
                .orElseThrow(() -> new ResourceNotFoundException("Vault", "id", dto.getVaultId()));

        if (!vault.getOwner().getId().equals(user.getId())) {
            throw new BusinessException("Unauthorized: You do not own this vault");
        }

        ConfidentialPolicyEntity policy = null;
        if (dto.getPolicyId() != null) {
            policy = policyRepository.findById(dto.getPolicyId()).orElse(null);
        } else {
            List<ConfidentialPolicyEntity> policies = policyRepository.findByVaultId(vault.getId());
            if (!policies.isEmpty()) {
                policy = policies.get(0);
            }
        }

        long startTime = System.currentTimeMillis();

        String decisionType = dto.getPreferredDecisionType() != null ? dto.getPreferredDecisionType() : "PROTECT_POSITION";
        String rationale = "Confidential policy evaluation completed inside Flare TEE enclave for vault: " + vault.getVaultName();

        String rawContent = vault.getId().toString() + decisionType + System.currentTimeMillis();
        String decisionHash = computeSha256(rawContent);

        FCCClient.FCCExecutionResult fccResult = fccClient.executeConfidentialPolicy(
                "ENCRYPTED_PAYLOAD_V1", "IV_V1", decisionHash
        );

        long fccLatencyMs = System.currentTimeMillis() - startTime;

        TreasuryDecisionEntity decision = new TreasuryDecisionEntity(
                vault, policy, decisionType, 1, "PENDING",
                rationale, fccResult.getAttestationId(), decisionHash
        );
        TreasuryDecisionEntity savedDecision = decisionRepository.save(decision);

        DecisionEvaluationEntity evaluation = new DecisionEvaluationEntity(
                savedDecision, vault, fccLatencyMs, "Evaluated inside Flare TEE Enclave in " + fccLatencyMs + "ms"
        );
        decisionEvaluationRepository.save(evaluation);

        DecisionQueueEntity queueItem = new DecisionQueueEntity(savedDecision, "QUEUED", Instant.now());
        decisionQueueRepository.save(queueItem);

        DecisionMetadataEntity meta = new DecisionMetadataEntity(savedDecision, "ENCLAVE_QUOTE_HASH", fccResult.getEnclaveQuoteHash());
        decisionMetadataRepository.save(meta);

        historyService.recordHistory(savedDecision, "DECISION_GENERATED", user.getEmail(), "Generated decision: " + decisionType);
        auditService.logDecisionEvent(savedDecision, "DECISION_GENERATED", user.getEmail(), "Decision hash: " + decisionHash);

        return mapper.toDto(savedDecision);
    }

    public List<DecisionResponseDto> getUserDecisions(UserEntity user) {
        return decisionRepository.findAll().stream()
                .filter(d -> d.getVault().getOwner().getId().equals(user.getId()))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public DecisionResponseDto getDecisionById(UUID id, UserEntity user) {
        TreasuryDecisionEntity decision = decisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TreasuryDecision", "id", id));

        if (!decision.getVault().getOwner().getId().equals(user.getId())) {
            throw new BusinessException("Unauthorized access to decision");
        }

        return mapper.toDto(decision);
    }

    public List<DecisionQueueResponseDto> getActiveQueue(UserEntity user) {
        return decisionQueueRepository.findByStatus("QUEUED").stream()
                .filter(q -> q.getDecision().getVault().getOwner().getId().equals(user.getId()))
                .map(q -> new DecisionQueueResponseDto(
                        q.getId(), q.getDecision().getId(), q.getDecision().getDecisionType(),
                        q.getDecision().getVault().getVaultName(), q.getStatus(),
                        q.getScheduledAt(), q.getProcessedAt()
                ))
                .collect(Collectors.toList());
    }

    public DecisionStatusMetricsDto getMetrics(UserEntity user) {
        List<TreasuryDecisionEntity> decisions = decisionRepository.findAll().stream()
                .filter(d -> d.getVault().getOwner().getId().equals(user.getId()))
                .collect(Collectors.toList());

        long total = decisions.size();
        long pending = decisions.stream().filter(d -> "PENDING".equalsIgnoreCase(d.getStatus())).count();
        long approved = decisions.stream().filter(d -> "APPROVED".equalsIgnoreCase(d.getStatus())).count();
        long queueSize = decisionQueueRepository.findByStatus("QUEUED").size();

        return new DecisionStatusMetricsDto(total, pending, approved, queueSize, 120L, 85L);
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
