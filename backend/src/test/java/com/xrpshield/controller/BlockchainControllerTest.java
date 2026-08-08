package com.xrpshield.controller;

import com.xrpshield.blockchain.BlockchainClient;
import com.xrpshield.blockchain.BlockchainConfiguration;
import com.xrpshield.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BlockchainControllerTest {

    private BlockchainController blockchainController;

    @BeforeEach
    void setUp() {
        BlockchainConfiguration config = new BlockchainConfiguration();
        BlockchainClient client = new BlockchainClient(config) {
            @Override
            public BigInteger getLatestBlockNumber() {
                return BigInteger.valueOf(18492310L);
            }

            @Override
            public BigInteger getGasPrice() {
                return BigInteger.valueOf(22500000000L);
            }
        };

        blockchainController = new BlockchainController(null, client, null, config);
    }

    @Test
    @DisplayName("Should return 200 OK and latest block information")
    void testGetLatestBlock() {
        ResponseEntity<ApiResponse<Map<String, Object>>> response = blockchainController.getLatestBlock();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(BigInteger.valueOf(18492310L), response.getBody().getData().get("blockNumber"));
    }
}

