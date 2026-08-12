package com.xrpshield.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExecutionMonitoringService {

    public static class ExecutionRecord {
        public String vaultId;
        public String policyCommitment;
        public String instructionId;
        public String transactionHash;
        public long blockNumber;
        public BigDecimal amountFXRP;
        public BigDecimal amountUSDT0;
        public String routerAddress;
        public String executionStatus; // "STARTED", "EXECUTED", "FAILED"
        public long timestamp;

        public ExecutionRecord(String vaultId, String policyCommitment, String instructionId, String transactionHash, long blockNumber, BigDecimal amountFXRP, BigDecimal amountUSDT0, String routerAddress, String executionStatus, long timestamp) {
            this.vaultId = vaultId;
            this.policyCommitment = policyCommitment;
            this.instructionId = instructionId;
            this.transactionHash = transactionHash;
            this.blockNumber = blockNumber;
            this.amountFXRP = amountFXRP;
            this.amountUSDT0 = amountUSDT0;
            this.routerAddress = routerAddress;
            this.executionStatus = executionStatus;
            this.timestamp = timestamp;
        }
    }

    private final Map<String, ExecutionRecord> executionRecords = new ConcurrentHashMap<>();

    public ExecutionRecord recordExecutionEvent(String instructionId, String vaultId, String policyCommitment, String transactionHash, long blockNumber, BigDecimal amountFXRP, BigDecimal amountUSDT0, String routerAddress, String status) {
        long timestamp = Instant.now().getEpochSecond();
        ExecutionRecord record = new ExecutionRecord(
                vaultId, policyCommitment, instructionId, transactionHash, blockNumber, amountFXRP, amountUSDT0, routerAddress, status, timestamp
        );
        executionRecords.put(instructionId, record);
        return record;
    }

    public ExecutionRecord getExecutionRecord(String instructionId) {
        return executionRecords.get(instructionId);
    }

    public List<ExecutionRecord> getAllExecutionRecords() {
        return new ArrayList<>(executionRecords.values());
    }
}
