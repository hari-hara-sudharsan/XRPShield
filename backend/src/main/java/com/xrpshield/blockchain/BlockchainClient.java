package com.xrpshield.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;

@Component
public class BlockchainClient {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainClient.class);

    private final BlockchainConfiguration config;
    private final Web3j web3j;

    public BlockchainClient(BlockchainConfiguration config) {
        this.config = config;
        this.web3j = Web3j.build(new HttpService(config.getRpcUrl()));
        logger.info("Web3j Blockchain Client initialized targeting RPC: {}", config.getRpcUrl());
    }

    public BlockchainConfiguration getConfig() {
        return config;
    }

    public Web3j getWeb3j() {
        return web3j;
    }


    public BigInteger getLatestBlockNumber() {
        try {
            return web3j.ethBlockNumber().send().getBlockNumber();
        } catch (Exception e) {
            logger.error("Failed to fetch block number from Flare RPC: {}", e.getMessage());
            return BigInteger.ZERO;
        }
    }

    public BigInteger getGasPrice() {
        try {
            return web3j.ethGasPrice().send().getGasPrice();
        } catch (Exception e) {
            logger.error("Failed to fetch gas price from Flare RPC: {}", e.getMessage());
            return BigInteger.ZERO;
        }
    }

    public EthBlock.Block getLatestBlock() {
        try {
            return web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock();
        } catch (Exception e) {
            logger.error("Failed to fetch latest block details: {}", e.getMessage());
            return null;
        }
    }

    public boolean isConnected() {
        try {
            return web3j.netListening().send().isListening();
        } catch (Exception e) {
            return false;
        }
    }
}
