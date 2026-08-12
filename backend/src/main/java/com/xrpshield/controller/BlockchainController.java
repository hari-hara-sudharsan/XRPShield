package com.xrpshield.controller;

import com.xrpshield.blockchain.BlockchainClient;
import com.xrpshield.blockchain.BlockchainConfiguration;
import com.xrpshield.blockchain.ContractService;
import com.xrpshield.blockchain.NetworkService;
import com.xrpshield.dto.ApiResponse;
import com.xrpshield.entity.ContractMetadataEntity;
import com.xrpshield.service.FXRPService;
import com.xrpshield.service.FlareContractRegistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Blockchain Infrastructure & Monitoring", description = "Endpoints for Flare network status, latest blocks, gas prices, contract registry, and Web3 RPC health")
public class BlockchainController {

    private final NetworkService networkService;
    private final BlockchainClient blockchainClient;
    private final ContractService contractService;
    private final BlockchainConfiguration config;
    private final FlareContractRegistryService flareRegistryService;
    private final FXRPService fxrpService;

    public BlockchainController(NetworkService networkService,
                                BlockchainClient blockchainClient,
                                ContractService contractService,
                                BlockchainConfiguration config,
                                FlareContractRegistryService flareRegistryService,
                                FXRPService fxrpService) {
        this.networkService = networkService;
        this.blockchainClient = blockchainClient;
        this.contractService = contractService;
        this.config = config;
        this.flareRegistryService = flareRegistryService;
        this.fxrpService = fxrpService;
    }

    @GetMapping({"/api/blockchain/network", "/api/v1/blockchain/network"})
    @Operation(summary = "Get Network Details", description = "Retrieves target network parameters (Flare Coston2 Testnet / Chain ID 114)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNetwork() {
        Map<String, Object> networkInfo = new HashMap<>();
        networkInfo.put("name", "Flare Coston2 Testnet");
        networkInfo.put("chainId", config.getChainId());
        networkInfo.put("rpcUrl", config.getRpcUrl());
        networkInfo.put("explorerUrl", "https://coston2-explorer.flare.network");
        networkInfo.put("nativeAsset", "C2FLR");
        networkInfo.put("contractRegistryAddress", config.getContractRegistryAddress());
        networkInfo.put("registryDetails", flareRegistryService.getFullRegistryStatus());

        return ResponseEntity.ok(ApiResponse.success("Network configuration retrieved from Coston2", networkInfo));
    }

    @GetMapping({"/api/blockchain/wallet/{address}", "/api/v1/blockchain/wallet/{address}"})
    @Operation(summary = "Get Real Wallet Details", description = "Retrieves actual native C2FLR balance and FXRP ERC-20 balance from Flare Coston2 node")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWalletDetails(@PathVariable String address) {
        BigInteger latestBlock = blockchainClient.getLatestBlockNumber();
        BigInteger fxrpBalance = fxrpService.getFXRPBalance(address);

        BigDecimal formattedFxrp = new BigDecimal(fxrpBalance)
                .divide(new BigDecimal("1000000000000000000"), 4, RoundingMode.HALF_UP);

        Map<String, Object> walletData = new HashMap<>();
        walletData.put("address", address);
        walletData.put("chainId", config.getChainId());
        walletData.put("network", "Flare Coston2 Testnet");
        walletData.put("latestBlockNumber", latestBlock);
        walletData.put("fxrpBalanceRaw", fxrpBalance.toString());
        walletData.put("fxrpBalanceFormatted", formattedFxrp.toPlainString() + " FXRP");
        walletData.put("isConnected", blockchainClient.isConnected());

        return ResponseEntity.ok(ApiResponse.success("Real Coston2 wallet state retrieved", walletData));
    }

    @GetMapping({"/api/blockchain/fxrp/{address}", "/api/v1/blockchain/fxrp/{address}"})
    @Operation(summary = "Get Real FXRP Token State", description = "Reads real Coston2 FXRP token contract balance and vault allowance for wallet address")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFXRPState(@PathVariable String address) {
        Map<String, Object> fxrpData = fxrpService.getFXRPTokenDetails(address);
        return ResponseEntity.ok(ApiResponse.success("Real Coston2 FXRP state retrieved", fxrpData));
    }

    @GetMapping({"/api/blockchain/status", "/api/v1/blockchain/status"})
    @Operation(summary = "Get Blockchain Status", description = "Retrieves connected network parameters, chain ID, RPC responsiveness, and latest block height")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatus() {
        Map<String, Object> status = networkService.getNetworkStatus();
        return ResponseEntity.ok(ApiResponse.success("Blockchain status retrieved", status));
    }

    @GetMapping({"/api/blockchain/contracts", "/api/v1/blockchain/contracts"})
    @Operation(summary = "Get Deployed Contract Metadata", description = "Retrieves deployed smart contract addresses and system metadata")
    public ResponseEntity<ApiResponse<List<ContractMetadataEntity>>> getContracts() {
        List<ContractMetadataEntity> contracts = contractService.getDeployedContracts();
        return ResponseEntity.ok(ApiResponse.success("Contract metadata retrieved", contracts));
    }

    @GetMapping({"/api/blockchain/health", "/api/v1/blockchain/health"})
    @Operation(summary = "Blockchain Connectivity Health Check", description = "Verifies active connection to Flare RPC node")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHealth() {
        boolean connected = blockchainClient.isConnected();
        BigInteger blockNumber = blockchainClient.getLatestBlockNumber();

        Map<String, Object> health = new HashMap<>();
        health.put("status", connected ? "UP" : "DOWN");
        health.put("latestBlock", blockNumber);
        health.put("rpcUrl", config.getRpcUrl());
        health.put("timestamp", Instant.now());

        return ResponseEntity.ok(ApiResponse.success("Blockchain health operational", health));
    }
}
