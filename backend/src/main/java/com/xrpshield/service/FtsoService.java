package com.xrpshield.service;

import com.xrpshield.blockchain.BlockchainClient;
import com.xrpshield.dto.MarketPriceResponseDto;
import com.xrpshield.entity.MarketPriceSnapshotEntity;
import com.xrpshield.exception.BusinessException;
import com.xrpshield.repository.MarketPriceSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Instant;

@Service
public class FtsoService {

    private static final Logger logger = LoggerFactory.getLogger(FtsoService.class);

    private static final String FTSOV2_CONTRACT_ADDRESS = "0xC4e9c78EA53db782E28f28Fdf80BaF59336B304d";
    private static final String XRP_USD_FEED_ID = "0x015852502f55534400000000000000000000000000";
    private static final String GET_FEED_BY_ID_SELECTOR = "0x93e9f806";
    private static final long STALE_THRESHOLD_SECONDS = 180; // 3 minutes

    private final BlockchainClient blockchainClient;
    private final MarketPriceSnapshotRepository snapshotRepository;

    public FtsoService(BlockchainClient blockchainClient, MarketPriceSnapshotRepository snapshotRepository) {
        this.blockchainClient = blockchainClient;
        this.snapshotRepository = snapshotRepository;
    }

    @Transactional
    public MarketPriceResponseDto fetchLiveXRPUSDPrice() {
        logger.info("Querying Flare FTSOv2 contract at {} for XRP/USD feed {}", FTSOV2_CONTRACT_ADDRESS, XRP_USD_FEED_ID);

        try {
            // Function selector: 0x93e9f806
            // Argument: bytes21 padded right to 32 bytes (64 hex characters)
            String paddedFeedId = XRP_USD_FEED_ID.substring(2) + "00000000000000000000000000";
            String dataHex = GET_FEED_BY_ID_SELECTOR + paddedFeedId;

            Transaction transaction = Transaction.createEthCallTransaction(null, FTSOV2_CONTRACT_ADDRESS, dataHex);
            EthCall response = blockchainClient.getWeb3j().ethCall(transaction, DefaultBlockParameterName.LATEST).send();

            if (response.hasError() || response.getValue() == null || response.getValue().equals("0x")) {
                logger.error("Failed to fetch FTSOv2 price feed: {}", response.getError() != null ? response.getError().getMessage() : "Empty response");
                throw new BusinessException("ERROR: Flare FTSOv2 XRP/USD price feed is unreachable on Coston2 Testnet.");
            }

            String hexResult = response.getValue().substring(2);
            if (hexResult.length() < 192) { // 3 slots * 64 chars = 192
                throw new BusinessException("ERROR: Invalid ABI response format from FTSOv2 contract.");
            }

            BigInteger rawVal = new BigInteger(hexResult.substring(0, 64), 16);
            BigInteger decimalsVal = new BigInteger(hexResult.substring(64, 128), 16);
            BigInteger timestampVal = new BigInteger(hexResult.substring(128, 192), 16);

            int decimals = decimalsVal.intValue();
            long feedTimestamp = timestampVal.longValue();

            BigDecimal price = new BigDecimal(rawVal).divide(BigDecimal.TEN.pow(decimals), 8, RoundingMode.HALF_UP);
            long currentEpoch = Instant.now().getEpochSecond();
            long freshnessSeconds = Math.max(0, currentEpoch - feedTimestamp);
            boolean stale = freshnessSeconds > STALE_THRESHOLD_SECONDS;

            logger.info("Real FTSOv2 XRP/USD Price: ${} | Raw: {} | Decimals: {} | Timestamp: {} | Freshness: {}s", price, rawVal, decimals, feedTimestamp, freshnessSeconds);

            MarketPriceSnapshotEntity snapshot = new MarketPriceSnapshotEntity(
                    "XRP/USD", price, rawVal.longValue(), decimals, feedTimestamp, XRP_USD_FEED_ID, "Flare FTSOv2", stale
            );
            snapshotRepository.save(snapshot);

            return new MarketPriceResponseDto(price, rawVal.longValue(), decimals, feedTimestamp, XRP_USD_FEED_ID, "Flare FTSOv2", stale, freshnessSeconds);

        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            logger.error("Critical FTSOv2 RPC Exception: {}", e.getMessage(), e);
            throw new BusinessException("ERROR: Failed to query Flare FTSOv2 price feed from Coston2 RPC.", e);
        }
    }
}
