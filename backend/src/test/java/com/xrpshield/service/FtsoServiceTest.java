package com.xrpshield.service;

import com.xrpshield.blockchain.BlockchainClient;
import com.xrpshield.blockchain.BlockchainConfiguration;
import com.xrpshield.dto.MarketPriceResponseDto;
import com.xrpshield.exception.BusinessException;
import com.xrpshield.repository.MarketPriceSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthCall;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FtsoServiceTest {

    private BlockchainClient blockchainClient;
    private MarketPriceSnapshotRepository snapshotRepository;
    private FlareContractRegistryService flareRegistryService;
    private FtsoService ftsoService;
    private Web3j web3jMock;

    @BeforeEach
    void setUp() {
        blockchainClient = Mockito.mock(BlockchainClient.class);
        snapshotRepository = Mockito.mock(MarketPriceSnapshotRepository.class);
        flareRegistryService = Mockito.mock(FlareContractRegistryService.class);
        web3jMock = Mockito.mock(Web3j.class);

        when(blockchainClient.getWeb3j()).thenReturn(web3jMock);
        when(flareRegistryService.resolveContractAddress("FtsoV2")).thenReturn("0xC4e9c78EA53db782E28f28Fdf80BaF59336B304d");

        ftsoService = new FtsoService(blockchainClient, snapshotRepository, flareRegistryService);
    }

    @Test
    @DisplayName("1. Should successfully parse valid FTSOv2 price feed response")
    void testValidPriceFeed() throws Exception {
        EthCall ethCallMock = new EthCall();
        // 584200 (price) | 6 (decimals) | timestamp (current epoch)
        long now = System.currentTimeMillis() / 1000;
        String hexVal = String.format("0x%064x%064x%064x", 584200L, 6L, now);
        ethCallMock.setResult(hexVal);

        Request ethCallRequest = Mockito.mock(Request.class);
        when(ethCallRequest.send()).thenReturn(ethCallMock);
        when(web3jMock.ethCall(any(), any())).thenReturn(ethCallRequest);

        MarketPriceResponseDto response = ftsoService.fetchLiveXRPUSDPrice();

        assertNotNull(response);
        assertEquals(new BigDecimal("0.58420000"), response.getPrice());
        assertEquals(6, response.getDecimals());
        assertEquals("Flare FTSOv2", response.getSource());
        assertFalse(response.isStale());
    }

    @Test
    @DisplayName("2. Should flag price response as STALE if timestamp > 180 seconds ago")
    void testStalePriceFeed() throws Exception {
        EthCall ethCallMock = new EthCall();
        // Timestamp from 10 minutes (600s) ago
        long oldTimestamp = (System.currentTimeMillis() / 1000) - 600;
        String hexVal = String.format("0x%064x%064x%064x", 584200L, 6L, oldTimestamp);
        ethCallMock.setResult(hexVal);

        Request ethCallRequest = Mockito.mock(Request.class);
        when(ethCallRequest.send()).thenReturn(ethCallMock);
        when(web3jMock.ethCall(any(), any())).thenReturn(ethCallRequest);

        MarketPriceResponseDto response = ftsoService.fetchLiveXRPUSDPrice();

        assertNotNull(response);
        assertTrue(response.isStale());
    }

    @Test
    @DisplayName("3. Should throw BusinessException on invalid ABI feed response format")
    void testInvalidFeedResponse() throws Exception {
        EthCall ethCallMock = new EthCall();
        ethCallMock.setResult("0x1234"); // Too short ABI

        Request ethCallRequest = Mockito.mock(Request.class);
        when(ethCallRequest.send()).thenReturn(ethCallMock);
        when(web3jMock.ethCall(any(), any())).thenReturn(ethCallRequest);

        assertThrows(BusinessException.class, () -> ftsoService.fetchLiveXRPUSDPrice());
    }

    @Test
    @DisplayName("4. Should throw BusinessException on RPC node failure")
    void testRpcFailure() throws Exception {
        Request ethCallRequest = Mockito.mock(Request.class);
        when(ethCallRequest.send()).thenThrow(new RuntimeException("RPC Connection Timeout"));
        when(web3jMock.ethCall(any(), any())).thenReturn(ethCallRequest);

        assertThrows(BusinessException.class, () -> ftsoService.fetchLiveXRPUSDPrice());
    }
}
