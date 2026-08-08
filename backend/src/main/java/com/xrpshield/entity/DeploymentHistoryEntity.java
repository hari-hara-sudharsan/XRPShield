package com.xrpshield.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "deployment_history")
public class DeploymentHistoryEntity extends BaseEntity {

    @Column(name = "contract_name", nullable = false, length = 100)
    private String contractName;

    @Column(name = "contract_address", nullable = false, length = 64)
    private String contractAddress;

    @Column(name = "tx_hash", unique = true, length = 66)
    private String txHash;

    @Column(name = "deployer_address", nullable = false, length = 64)
    private String deployerAddress;

    @Column(name = "gas_used")
    private Long gasUsed;

    @Column(name = "network", nullable = false, length = 50)
    private String network;

    public DeploymentHistoryEntity() {}

    public DeploymentHistoryEntity(String contractName, String contractAddress, String txHash, String deployerAddress, Long gasUsed, String network) {
        this.contractName = contractName;
        this.contractAddress = contractAddress;
        this.txHash = txHash;
        this.deployerAddress = deployerAddress;
        this.gasUsed = gasUsed;
        this.network = network;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getDeployerAddress() {
        return deployerAddress;
    }

    public void setDeployerAddress(String deployerAddress) {
        this.deployerAddress = deployerAddress;
    }

    public Long getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(Long gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
}
