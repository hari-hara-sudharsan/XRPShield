package com.xrpshield.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigInteger;

@Entity
@Table(name = "blockchain_transaction_logs")
public class BlockchainTransactionLogEntity extends BaseEntity {

    @Column(name = "tx_hash", nullable = false, unique = true, length = 66)
    private String txHash;

    @Column(name = "from_address", nullable = false, length = 64)
    private String fromAddress;

    @Column(name = "to_address", length = 64)
    private String toAddress;

    @Column(name = "block_number")
    private Long blockNumber;

    @Column(name = "gas_price")
    private BigInteger gasPrice;

    @Column(name = "gas_used")
    private Long gasUsed;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    public BlockchainTransactionLogEntity() {}

    public BlockchainTransactionLogEntity(String txHash, String fromAddress, String toAddress, Long blockNumber, BigInteger gasPrice, Long gasUsed, String status) {
        this.txHash = txHash;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.blockNumber = blockNumber;
        this.gasPrice = gasPrice;
        this.gasUsed = gasUsed;
        this.status = status;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public Long getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(Long gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
