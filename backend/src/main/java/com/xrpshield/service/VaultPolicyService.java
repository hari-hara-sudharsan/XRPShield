package com.xrpshield.service;

import com.xrpshield.dto.VaultPolicyRequestDto;
import com.xrpshield.dto.VaultPolicyResponseDto;
import com.xrpshield.entity.PolicyStatus;
import com.xrpshield.entity.VaultEntity;
import com.xrpshield.entity.VaultPolicyEntity;
import com.xrpshield.exception.ResourceNotFoundException;
import com.xrpshield.mapper.VaultPolicyMapper;
import com.xrpshield.repository.VaultPolicyRepository;
import com.xrpshield.repository.VaultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VaultPolicyService {

    private static final Logger logger = LoggerFactory.getLogger(VaultPolicyService.class);

    private final VaultPolicyRepository vaultPolicyRepository;
    private final VaultRepository vaultRepository;
    private final VaultPolicyMapper vaultPolicyMapper;

    public VaultPolicyService(VaultPolicyRepository vaultPolicyRepository, VaultRepository vaultRepository, VaultPolicyMapper vaultPolicyMapper) {
        this.vaultPolicyRepository = vaultPolicyRepository;
        this.vaultRepository = vaultRepository;
        this.vaultPolicyMapper = vaultPolicyMapper;
    }

    public VaultPolicyResponseDto createPolicy(VaultPolicyRequestDto request) {
        logger.info("Creating policy {} for vault {}", request.getPolicyName(), request.getVaultId());
        VaultEntity vault = vaultRepository.findById(request.getVaultId())
                .orElseThrow(() -> new ResourceNotFoundException("Vault", "id", request.getVaultId()));

        VaultPolicyEntity policy = new VaultPolicyEntity(vault, request.getPolicyName(), request.getDescription(), request.getConfidentialHash(), request.getExecutionTrigger(), PolicyStatus.ACTIVE);
        VaultPolicyEntity saved = vaultPolicyRepository.save(policy);
        return vaultPolicyMapper.toDto(saved);
    }

    public VaultPolicyResponseDto getPolicyById(UUID id) {
        VaultPolicyEntity policy = vaultPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VaultPolicy", "id", id));
        return vaultPolicyMapper.toDto(policy);
    }

    public List<VaultPolicyResponseDto> getPoliciesByVaultId(UUID vaultId) {
        return vaultPolicyRepository.findByVaultId(vaultId).stream()
                .map(vaultPolicyMapper::toDto)
                .collect(Collectors.toList());
    }
}
