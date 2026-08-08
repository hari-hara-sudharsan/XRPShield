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
        return fccClient.executeConfidentialPolicy(
                encryptedPolicy.getEncryptedPayload(),
                encryptedPolicy.getIv(),
                policy.getPolicyHash()
        );
    }
}
