package com.xrpshield.blockchain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlockchainConfiguration {

    @Value("${xrpshield.flare.rpc-url:https://coston2-api.flare.network/ext/C/rpc}")
    private String rpcUrl;

    @Value("${xrpshield.flare.chain-id:114}")
    private Long chainId;

    @Value("${xrpshield.flare.vault-manager-address:0x0000000000000000000000000000000000000000}")
    private String vaultManagerAddress;

    @Value("${xrpshield.flare.access-manager-address:0x0000000000000000000000000000000000000000}")
    private String accessManagerAddress;

    @Value("${xrpshield.flare.treasury-storage-address:0x0000000000000000000000000000000000000000}")
    private String treasuryStorageAddress;

    public String getRpcUrl() {
        return rpcUrl;
    }

    public void setRpcUrl(String rpcUrl) {
        this.rpcUrl = rpcUrl;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public String getVaultManagerAddress() {
        return vaultManagerAddress;
    }

    public void setVaultManagerAddress(String vaultManagerAddress) {
        this.vaultManagerAddress = vaultManagerAddress;
    }

    public String getAccessManagerAddress() {
        return accessManagerAddress;
    }

    public void setAccessManagerAddress(String accessManagerAddress) {
        this.accessManagerAddress = accessManagerAddress;
    }

    public String getTreasuryStorageAddress() {
        return treasuryStorageAddress;
    }

    public void setTreasuryStorageAddress(String treasuryStorageAddress) {
        this.treasuryStorageAddress = treasuryStorageAddress;
    }
}
