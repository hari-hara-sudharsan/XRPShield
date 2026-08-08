package com.xrpshield.blockchain;

import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Service
public class NetworkService {

    private final BlockchainClient blockchainClient;
    private final BlockchainConfiguration config;

    public NetworkService(BlockchainClient blockchainClient, BlockchainConfiguration config) {
        this.blockchainClient = blockchainClient;
        this.config = config;
    }

    public Map<String, Object> getNetworkStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("networkName", "Flare Coston2 Testnet");
        status.put("chainId", config.getChainId());
        status.put("rpcUrl", config.getRpcUrl());
        status.put("isConnected", blockchainClient.isConnected());

        BigInteger blockNum = blockchainClient.getLatestBlockNumber();
        status.put("latestBlockNumber", blockNum);

        BigInteger gasPrice = blockchainClient.getGasPrice();
        status.put("gasPriceWei", gasPrice);

        return status;
    }
}
