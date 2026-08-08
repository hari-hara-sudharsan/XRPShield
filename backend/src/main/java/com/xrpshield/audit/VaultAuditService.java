package com.xrpshield.audit;

import com.xrpshield.entity.VaultHistoryEntity;
import com.xrpshield.entity.VaultEntity;
import com.xrpshield.repository.VaultHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VaultAuditService {

    private static final Logger logger = LoggerFactory.getLogger(VaultAuditService.class);

    private final VaultHistoryRepository vaultHistoryRepository;

    public VaultAuditService(VaultHistoryRepository vaultHistoryRepository) {
        this.vaultHistoryRepository = vaultHistoryRepository;
    }

    public void logVaultActivity(VaultEntity vault, String action, String actorAddress, String details) {
        logger.info("VAULT_AUDIT_LOG | Vault: {} | Action: {} | Actor: {} | Details: {}",
                vault.getId(), action, actorAddress, details);

        VaultHistoryEntity history = new VaultHistoryEntity(vault, action, actorAddress, details);
        vaultHistoryRepository.save(history);
    }
}
