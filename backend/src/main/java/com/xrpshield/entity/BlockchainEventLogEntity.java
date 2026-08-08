package com.xrpshield.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "blockchain_event_logs")
public class BlockchainEventLogEntity extends BaseEntity {

    @Column(name = "event_name", nullable = false, length = 100)
    private String eventName;

    @Column(name = "contract_address", nullable = false, length = 64)
    private String contractAddress;

    @Column(name = "tx_hash", nullable = false, length = 66)
    private String txHash;

    @Column(name = "block_number", nullable = false)
    private Long blockNumber;

    @Column(name = "log_index", nullable = false)
    private Integer logIndex;

    @Column(name = "event_data", columnDefinition = "TEXT")
    private String eventData;

    public BlockchainEventLogEntity() {}

    public BlockchainEventLogEntity(String eventName, String contractAddress, String txHash, Long blockNumber, Integer logIndex, String eventData) {
        this.eventName = eventName;
        this.contractAddress = contractAddress;
        this.txHash = txHash;
        this.blockNumber = blockNumber;
        this.logIndex = logIndex;
        this.eventData = eventData;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
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

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public Integer getLogIndex() {
        return logIndex;
    }

    public void setLogIndex(Integer logIndex) {
        this.logIndex = logIndex;
    }

    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }
}
