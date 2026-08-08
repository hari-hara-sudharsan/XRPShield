package com.xrpshield.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.Optional;

@Service
public class ReceiptService {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptService.class);

    private final BlockchainClient blockchainClient;

    public ReceiptService(BlockchainClient blockchainClient) {
        this.blockchainClient = blockchainClient;
    }

    public Optional<TransactionReceipt> getTransactionReceipt(String txHash) {
        try {
            return blockchainClient.getWeb3j().ethGetTransactionReceipt(txHash).send().getTransactionReceipt();
        } catch (Exception e) {
            logger.error("Error fetching transaction receipt for tx {}: {}", txHash, e.getMessage());
            return Optional.empty();
        }
    }
}
