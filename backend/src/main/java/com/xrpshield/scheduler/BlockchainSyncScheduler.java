package com.xrpshield.scheduler;

import com.xrpshield.blockchain.BlockchainClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class BlockchainSyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainSyncScheduler.class);

    private final BlockchainClient blockchainClient;

    public BlockchainSyncScheduler(BlockchainClient blockchainClient) {
        this.blockchainClient = blockchainClient;
    }

    @Scheduled(fixedRate = 15000) // Sync every 15 seconds
    public void syncBlockchainState() {
        try {
            long startTime = System.currentTimeMillis();
            BigInteger latestBlock = blockchainClient.getLatestBlockNumber();
            long latency = System.currentTimeMillis() - startTime;

            logger.info("SCHEDULED_SYNC | Flare Coston2 Latest Block: #{} | RPC Latency: {}ms", latestBlock, latency);
        } catch (Exception e) {
            logger.warn("Scheduled blockchain sync failed: {}", e.getMessage());
        }
    }
}
