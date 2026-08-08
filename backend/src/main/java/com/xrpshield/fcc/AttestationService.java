package com.xrpshield.fcc;

import com.xrpshield.entity.ConfidentialPolicyEntity;
import com.xrpshield.entity.PolicyAttestationEntity;
import com.xrpshield.repository.PolicyAttestationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AttestationService {

    private static final Logger logger = LoggerFactory.getLogger(AttestationService.class);

    private final PolicyAttestationRepository attestationRepository;

    public AttestationService(PolicyAttestationRepository attestationRepository) {
        this.attestationRepository = attestationRepository;
    }

    public PolicyAttestationEntity recordAttestation(ConfidentialPolicyEntity policy, String attestationId, String enclaveQuoteHash) {
        logger.info("Recording Flare TEE Attestation proof {} for policy {}", attestationId, policy.getId());

        PolicyAttestationEntity attestation = new PolicyAttestationEntity(
                policy, attestationId, enclaveQuoteHash, "VERIFIED"
        );
        return attestationRepository.save(attestation);
    }

    public Optional<PolicyAttestationEntity> getLatestAttestation(UUID policyId) {
        return attestationRepository.findTopByPolicyIdOrderByAttestedAtDesc(policyId);
    }
}
