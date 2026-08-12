package com.xrpshield.blockchain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlockchainConfiguration {

    @Value("${xrpshield.flare.rpc-url:${FLARE_COSTON2_RPC_URL:https://coston2-api.flare.network/ext/C/rpc}}")
    private String rpcUrl;

    @Value("${xrpshield.flare.chain-id:${FLARE_CHAIN_ID:114}}")
    private Long chainId;

    @Value("${xrpshield.flare.contract-registry-address:0xaD6740B4F817109E96238bA722880b91e92dEec9}")
    private String contractRegistryAddress;

    @Value("${xrpshield.flare.fxrp-token-address:0x0d37e61a681dcf690ff33e7fd2918809989f664a}")
    private String fxrpTokenAddress;

    @Value("${xrpshield.flare.vault-manager-address:0x5bb8082987515f40398fb9893d90616b47c04208}")
    private String vaultManagerAddress;

    @Value("${xrpshield.flare.access-manager-address:0x0000000000000000000000000000000000000000}")
    private String accessManagerAddress;

    @Value("${xrpshield.flare.treasury-storage-address:0x0165878A594ca255338adfa4d48449f69242Eb8F}")
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

    public String getContractRegistryAddress() {
        return contractRegistryAddress;
    }

    public void setContractRegistryAddress(String contractRegistryAddress) {
        this.contractRegistryAddress = contractRegistryAddress;
    }

    public String getFxrpTokenAddress() {
        return fxrpTokenAddress;
    }

    public void setFxrpTokenAddress(String fxrpTokenAddress) {
        this.fxrpTokenAddress = fxrpTokenAddress;
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
