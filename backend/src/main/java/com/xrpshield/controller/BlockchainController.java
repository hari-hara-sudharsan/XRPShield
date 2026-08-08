package com.xrpshield.controller;

import com.xrpshield.blockchain.BlockchainClient;
import com.xrpshield.blockchain.BlockchainConfiguration;
import com.xrpshield.blockchain.ContractService;
import com.xrpshield.blockchain.NetworkService;
import com.xrpshield.dto.ApiResponse;
import com.xrpshield.entity.ContractMetadataEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/blockchain")
@Tag(name = "Blockchain Infrastructure & Monitoring", description = "Endpoints for Flare network status, latest blocks, gas prices, contract registry, and Web3 RPC health")
public class BlockchainController {

    private final NetworkService networkService;
    private final BlockchainClient blockchainClient;
    private final ContractService contractService;
    private final BlockchainConfiguration config;

    public BlockchainController(NetworkService networkService, BlockchainClient blockchainClient, ContractService contractService, BlockchainConfiguration config) {
        this.networkService = networkService;
        this.blockchainClient = blockchainClient;
        this.contractService = contractService;
        this.config = config;
    }

    @GetMapping("/status")
    @Operation(summary = "Get Blockchain Status", description = "Retrieves connected network parameters, chain ID, RPC responsiveness, and latest block height")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatus() {
        Map<String, Object> status = networkService.getNetworkStatus();
        return ResponseEntity.ok(ApiResponse.success("Blockchain status retrieved", status));
    }

    @GetMapping("/network")
    @Operation(summary = "Get Network Details", description = "Retrieves target network parameters (Flare Coston2 Testnet / Chain ID 114)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNetwork() {
        Map<String, Object> networkInfo = new HashMap<>();
        networkInfo.put("name", "Flare Coston2 Testnet");
        networkInfo.put("chainId", config.getChainId());
        networkInfo.put("rpcUrl", config.getRpcUrl());
        networkInfo.put("explorerUrl", "https://coston2-explorer.flare.network");
        networkInfo.put("nativeAsset", "CFLR");

        return ResponseEntity.ok(ApiResponse.success("Network configuration retrieved", networkInfo));
    }

    @GetMapping("/contracts")
    @Operation(summary = "Get Deployed Contract Metadata", description = "Retrieves deployed smart contract addresses and system metadata")
    public ResponseEntity<ApiResponse<List<ContractMetadataEntity>>> getContracts() {
        List<ContractMetadataEntity> contracts = contractService.getDeployedContracts();
        return ResponseEntity.ok(ApiResponse.success("Contract metadata retrieved", contracts));
    }

    @GetMapping("/health")
    @Operation(summary = "Blockchain Connectivity Health Check", description = "Verifies active connection to Flare RPC node")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHealth() {
        boolean connected = blockchainClient.isConnected();
        BigInteger blockNumber = blockchainClient.getLatestBlockNumber();

        Map<String, Object> health = new HashMap<>();
        health.put("status", connected ? "UP" : "DOWN");
        health.put("latestBlock", blockNumber);
        health.put("timestamp", Instant.now());

        return ResponseEntity.ok(ApiResponse.success("Blockchain health operational", health));
    }

    @GetMapping("/latest-block")
    @Operation(summary = "Get Latest Block Metadata", description = "Retrieves block number and current gas price")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLatestBlock() {
        BigInteger blockNumber = blockchainClient.getLatestBlockNumber();
        BigInteger gasPrice = blockchainClient.getGasPrice();

        Map<String, Object> blockData = new HashMap<>();
        blockData.put("blockNumber", blockNumber);
        blockData.put("gasPriceWei", gasPrice);
        blockData.put("timestamp", Instant.now());

        return ResponseEntity.ok(ApiResponse.success("Latest block metadata retrieved", blockData));
    }
}
