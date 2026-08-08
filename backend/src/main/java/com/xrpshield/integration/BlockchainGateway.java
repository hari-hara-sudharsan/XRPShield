package com.xrpshield.integration;

import com.xrpshield.blockchain.BlockchainClient;
import com.xrpshield.blockchain.BlockchainConfiguration;
import com.xrpshield.blockchain.ContractService;
import com.xrpshield.entity.ContractMetadataEntity;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BlockchainGateway {

    private final BlockchainClient blockchainClient;
    private final ContractService contractService;
    private final BlockchainConfiguration config;

    public BlockchainGateway(BlockchainClient blockchainClient, ContractService contractService, BlockchainConfiguration config) {
        this.blockchainClient = blockchainClient;
        this.contractService = contractService;
        this.config = config;
    }

    public Map<String, Object> getBlockchainSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("network", "Flare Coston2");
        summary.put("chainId", config.getChainId());
        summary.put("rpcUrl", config.getRpcUrl());
        summary.put("isConnected", blockchainClient.isConnected());

        long startTime = System.currentTimeMillis();
        BigInteger blockNumber = blockchainClient.getLatestBlockNumber();
        long latency = System.currentTimeMillis() - startTime;

        summary.put("latestBlock", blockNumber);
        summary.put("rpcLatencyMs", latency);
        summary.put("gasPriceWei", blockchainClient.getGasPrice());

        List<ContractMetadataEntity> contracts = contractService.getDeployedContracts();
        summary.put("deployedContractsCount", contracts.size());

        return summary;
    }
}
