package com.xrpshield.integration;

import com.xrpshield.repository.WalletRepository;
import com.xrpshield.wallet.WalletValidationUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WalletGateway {

    private final WalletRepository walletRepository;

    public WalletGateway(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Map<String, Object> getWalletSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("provider", "MetaMask Web3 Provider");
        summary.put("signatureScheme", "EIP-191 / EIP-4361");
        summary.put("linkedWalletsCount", walletRepository.count());
        return summary;
    }

    public boolean validateAddress(String address) {
        return WalletValidationUtils.isValidEthereumAddress(address) || WalletValidationUtils.isValidXrpAddress(address);
    }
}
