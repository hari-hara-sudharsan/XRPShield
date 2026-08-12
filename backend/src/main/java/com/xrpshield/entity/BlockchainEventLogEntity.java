package com.xrpshield.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "blockchain_event_logs", uniqueConstraints = {
    @UniqueConstraint(name = "uk_tx_hash_log_index", columnNames = {"transaction_hash", "log_index"})
})
public class BlockchainEventLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "transaction_hash", length = 66, nullable = false)
    private String transactionHash;

    @Column(name = "block_number", nullable = false)
    private Long blockNumber;

    @Column(name = "log_index", nullable = false)
    private Integer logIndex;

    @Column(name = "event_type", length = 64, nullable = false)
    private String eventType;

    @Column(name = "wallet_address", length = 42)
    private String walletAddress;

    @Column(name = "vault_id", length = 42)
    private String vaultId;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(name = "status", length = 32, nullable = false)
    private String status;

    @Column(name = "raw_payload", columnDefinition = "TEXT")
    private String rawPayload;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public BlockchainEventLogEntity() {}

    public BlockchainEventLogEntity(String transactionHash, Long blockNumber, Integer logIndex, String eventType, String walletAddress, String vaultId, LocalDateTime eventTimestamp, String status, String rawPayload) {
        this.transactionHash = transactionHash;
        this.blockNumber = blockNumber;
        this.logIndex = logIndex;
        this.eventType = eventType;
        this.walletAddress = walletAddress;
        this.vaultId = vaultId;
        this.eventTimestamp = eventTimestamp;
        this.status = status;
        this.rawPayload = rawPayload;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getVaultId() {
        return vaultId;
    }

    public void setVaultId(String vaultId) {
        this.vaultId = vaultId;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
