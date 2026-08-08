package com.xrpshield.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "network_configurations")
public class NetworkConfigurationEntity extends BaseEntity {

    @Column(name = "network_name", nullable = false, unique = true, length = 50)
    private String networkName;

    @Column(name = "chain_id", nullable = false, unique = true)
    private Long chainId;

    @Column(name = "rpc_url", nullable = false)
    private String rpcUrl;

    @Column(name = "explorer_url")
    private String explorerUrl;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public NetworkConfigurationEntity() {}

    public NetworkConfigurationEntity(String networkName, Long chainId, String rpcUrl, String explorerUrl, boolean isActive) {
        this.networkName = networkName;
        this.chainId = chainId;
        this.rpcUrl = rpcUrl;
        this.explorerUrl = explorerUrl;
        this.isActive = isActive;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public String getRpcUrl() {
        return rpcUrl;
    }

    public void setRpcUrl(String rpcUrl) {
        this.rpcUrl = rpcUrl;
    }

    public String getExplorerUrl() {
        return explorerUrl;
    }

    public void setExplorerUrl(String explorerUrl) {
        this.explorerUrl = explorerUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
