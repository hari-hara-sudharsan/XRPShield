package com.xrpshield.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "contract_metadata")
public class ContractMetadataEntity extends BaseEntity {

    @Column(name = "contract_name", nullable = false, length = 100)
    private String contractName;

    @Column(name = "contract_address", nullable = false, unique = true, length = 64)
    private String contractAddress;

    @Column(name = "abi_json", columnDefinition = "TEXT")
    private String abiJson;

    @Column(name = "network_name", nullable = false, length = 50)
    private String networkName = "Flare Coston2";

    @Column(name = "chain_id", nullable = false)
    private Long chainId = 114L;

    @Column(name = "deployed_block")
    private Long deployedBlock;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    public ContractMetadataEntity() {}

    public ContractMetadataEntity(String contractName, String contractAddress, String abiJson, String networkName, Long chainId, Long deployedBlock, String status) {
        this.contractName = contractName;
        this.contractAddress = contractAddress;
        this.abiJson = abiJson;
        this.networkName = networkName != null ? networkName : "Flare Coston2";
        this.chainId = chainId != null ? chainId : 114L;
        this.deployedBlock = deployedBlock;
        this.status = status != null ? status : "ACTIVE";
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

    public String getAbiJson() {
        return abiJson;
    }

    public void setAbiJson(String abiJson) {
        this.abiJson = abiJson;
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

    public Long getDeployedBlock() {
        return deployedBlock;
    }

    public void setDeployedBlock(Long deployedBlock) {
        this.deployedBlock = deployedBlock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
