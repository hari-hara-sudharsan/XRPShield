package com.xrpshield.service;

import com.xrpshield.audit.ExecutionAuditService;
import com.xrpshield.dto.*;
import com.xrpshield.entity.*;
import com.xrpshield.exception.BusinessException;
import com.xrpshield.exception.ResourceNotFoundException;
import com.xrpshield.gateway.ExecutionGateway;
import com.xrpshield.mapper.ExecutionMapper;
import com.xrpshield.repository.*;
import com.xrpshield.validator.ExecutionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionService.class);

    private final TreasuryExecutionRepository executionRepository;
    private final ExecutionHistoryRepository historyRepository;
    private final ExecutionQueueRepository queueRepository;
    private final ExecutionResultRepository resultRepository;
    private final ExecutionMetadataRepository metadataRepository;
    private final TreasuryDecisionRepository decisionRepository;
    private final ExecutionGateway executionGateway;
    private final ExecutionAuditService auditService;
    private final ExecutionValidator validator;
    private final ExecutionMapper mapper;

    public ExecutionService(TreasuryExecutionRepository executionRepository, ExecutionHistoryRepository historyRepository,
                            ExecutionQueueRepository queueRepository, ExecutionResultRepository resultRepository,
                            ExecutionMetadataRepository metadataRepository, TreasuryDecisionRepository decisionRepository,
                            ExecutionGateway executionGateway, ExecutionAuditService auditService,
                            ExecutionValidator validator, ExecutionMapper mapper) {
        this.executionRepository = executionRepository;
        this.historyRepository = historyRepository;
        this.queueRepository = queueRepository;
        this.resultRepository = resultRepository;
        this.metadataRepository = metadataRepository;
        this.decisionRepository = decisionRepository;
        this.executionGateway = executionGateway;
        this.auditService = auditService;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Transactional
    public ExecutionResponseDto startExecution(StartExecutionRequestDto dto, UserEntity user) {
        validator.validateStartRequest(dto);

        TreasuryDecisionEntity decision = decisionRepository.findById(dto.getDecisionId())
                .orElseThrow(() -> new ResourceNotFoundException("TreasuryDecision", "id", dto.getDecisionId()));

        validator.validateDecisionEligibility(decision);

        if (!decision.getVault().getOwner().getId().equals(user.getId())) {
            throw new BusinessException("Unauthorized: You do not own the target vault for execution");
        }

        Optional<TreasuryExecutionEntity> existing = executionRepository.findByDecisionId(decision.getId());
        if (existing.isPresent()) {
            throw new BusinessException("Execution already exists for decision: " + decision.getId());
        }

        String rawContent = decision.getId().toString() + System.currentTimeMillis();
        String executionHash = computeSha256(rawContent);

        TreasuryExecutionEntity execution = new TreasuryExecutionEntity(
                decision, decision.getVault(), "QUEUED", null, executionHash
        );
        TreasuryExecutionEntity savedExec = executionRepository.save(execution);

        ExecutionQueueEntity queueItem = new ExecutionQueueEntity(savedExec, 0, 3, "QUEUED", Instant.now());
        queueRepository.save(queueItem);

        ExecutionHistoryEntity history = new ExecutionHistoryEntity(
                savedExec, "QUEUED", user.getEmail(), "Protected execution queued for decision " + decision.getDecisionType()
        );
        historyRepository.save(history);

        auditService.logExecutionEvent(savedExec, "EXECUTION_REQUESTED", user.getEmail(), null, user.getEmail(), "Queued execution");

        ExecutionGateway.ExecutionTxResult txResult = executionGateway.executeOnChain(
                decision.getVault().getVaultAddress(), executionHash
        );

        savedExec.setExecutionState("COMPLETED");
        savedExec.setTxHash(txResult.getTxHash());
        savedExec.setBlockNumber(txResult.getBlockNumber());
        savedExec.setGasUsed(txResult.getGasUsed());
        savedExec.setCompletedAt(Instant.now());
        executionRepository.save(savedExec);

        ExecutionResultEntity resultEntity = new ExecutionResultEntity(
                savedExec, txResult.getStatus(), txResult.getPayload(), 85L, 1200L
        );
        resultRepository.save(resultEntity);

        metadataRepository.save(new ExecutionMetadataEntity(savedExec, "EXECUTION_MODE", "PROTECTED_TEE"));

        decision.setStatus("APPROVED");
        decisionRepository.save(decision);

        logger.debug("Execution completed successfully for ID: {}", savedExec.getId());
        auditService.logExecutionEvent(savedExec, "EXECUTION_COMPLETED", user.getEmail(), txResult.getTxHash(), user.getEmail(), "Completed execution on Flare Network");


        return mapper.toDto(savedExec);
    }

    @Transactional
    public ExecutionResponseDto cancelExecution(UUID executionId, UserEntity user) {
        TreasuryExecutionEntity execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new ResourceNotFoundException("TreasuryExecution", "id", executionId));

        if (!execution.getVault().getOwner().getId().equals(user.getId())) {
            throw new BusinessException("Unauthorized to cancel execution");
        }

        if ("COMPLETED".equalsIgnoreCase(execution.getExecutionState())) {
            throw new BusinessException("Cannot cancel an already completed execution");
        }

        execution.setExecutionState("CANCELLED");
        TreasuryExecutionEntity saved = executionRepository.save(execution);

        auditService.logExecutionEvent(saved, "EXECUTION_CANCELLED", user.getEmail(), saved.getTxHash(), user.getEmail(), "User cancelled execution");

        return mapper.toDto(saved);
    }

    public List<ExecutionResponseDto> getUserExecutions(UserEntity user) {
        return executionRepository.findAll().stream()
                .filter(e -> e.getVault().getOwner().getId().equals(user.getId()))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public ExecutionResponseDto getExecutionById(UUID id, UserEntity user) {
        TreasuryExecutionEntity execution = executionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TreasuryExecution", "id", id));

        if (!execution.getVault().getOwner().getId().equals(user.getId())) {
            throw new BusinessException("Unauthorized access to execution");
        }

        return mapper.toDto(execution);
    }

    public List<ExecutionHistoryEntity> getExecutionHistory(UUID executionId) {
        return historyRepository.findByExecutionIdOrderByCreatedAtDesc(executionId);
    }

    public List<ExecutionQueueResponseDto> getActiveQueue(UserEntity user) {
        return queueRepository.findByStatus("QUEUED").stream()
                .filter(q -> q.getExecution().getVault().getOwner().getId().equals(user.getId()))
                .map(q -> new ExecutionQueueResponseDto(
                        q.getId(), q.getExecution().getId(), q.getExecution().getVault().getVaultName(),
                        q.getRetryCount(), q.getMaxRetries(), q.getStatus(), q.getScheduledAt(), q.getProcessedAt()
                ))
                .collect(Collectors.toList());
    }

    public ExecutionStatusMetricsDto getMetrics(UserEntity user) {
        List<TreasuryExecutionEntity> executions = executionRepository.findAll().stream()
                .filter(e -> e.getVault().getOwner().getId().equals(user.getId()))
                .collect(Collectors.toList());

        long total = executions.size();
        long completed = executions.stream().filter(e -> "COMPLETED".equalsIgnoreCase(e.getExecutionState())).count();
        long failed = executions.stream().filter(e -> "FAILED".equalsIgnoreCase(e.getExecutionState())).count();
        long queueSize = queueRepository.findByStatus("QUEUED").size();
        double successRate = total > 0 ? ((double) completed / total) * 100.0 : 100.0;

        return new ExecutionStatusMetricsDto(total, completed, failed, queueSize, successRate, 1200L);
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
