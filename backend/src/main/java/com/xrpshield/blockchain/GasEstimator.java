package com.xrpshield.blockchain;

import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class GasEstimator {

    private final BlockchainClient blockchainClient;

    public GasEstimator(BlockchainClient blockchainClient) {
        this.blockchainClient = blockchainClient;
    }

    public BigInteger getCurrentGasPrice() {
        return blockchainClient.getGasPrice();
    }

    public BigInteger estimateStandardGasLimit() {
        return BigInteger.valueOf(210000L);
    }
}
