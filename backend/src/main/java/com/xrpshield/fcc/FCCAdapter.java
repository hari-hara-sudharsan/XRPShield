package com.xrpshield.fcc;

import com.xrpshield.entity.ConfidentialPolicyEntity;
import com.xrpshield.entity.EncryptedPolicyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FCCAdapter {

    private static final Logger logger = LoggerFactory.getLogger(FCCAdapter.class);

    private final FCCClient fccClient;

    public FCCAdapter(FCCClient fccClient) {
        this.fccClient = fccClient;
    }

    public FCCClient.FCCExecutionResult submitToEnclave(ConfidentialPolicyEntity policy, EncryptedPolicyEntity encryptedPolicy) {
        logger.info("FCCAdapter: Submitting policy {} (Hash: {}) to Flare TEE Enclave", policy.getPolicyName(), policy.getPolicyHash());
        String vaultAddress = policy.getVault() != null ? policy.getVault().getVaultAddress() : "0x5bb8082987515f40398fb9893d90616b47c04208";
        return fccClient.executeConfidentialPolicy(
                vaultAddress,
                policy.getPolicyHash(),
                "1.0225",
                "100000"
        );
    }
}
