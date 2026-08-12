package com.xrpshield.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@Service
public class DexQuoteService {

    public static final String COSTON2_ROUTER_ADDRESS = "0x600109D9cDe3267E1408892f39C27DBdf8dd6B4B";
    public static final String FXRP_ADDRESS = "0xC04E1A9D4e2f6B72A6bca2626e2E505A415c81b4";
    public static final String USDT0_ADDRESS = "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780";
    public static final long QUOTE_VALIDITY_SECONDS = 60;

    public Map<String, Object> calculateQuote(BigDecimal amountIn, BigDecimal maxSlippagePercent, BigDecimal currentXrpPriceUsd) {
        long timestamp = Instant.now().getEpochSecond();
        long deadline = timestamp + 300; // 5 minutes deadline

        if (amountIn == null || amountIn.compareTo(BigDecimal.ZERO) <= 0) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("status", "INVALID_INPUT");
            err.put("error", "Amount in must be positive");
            return err;
        }

        BigDecimal slippage = maxSlippagePercent != null ? maxSlippagePercent : new BigDecimal("0.50");
        BigDecimal xrpPrice = currentXrpPriceUsd != null && currentXrpPriceUsd.compareTo(BigDecimal.ZERO) > 0 
                ? currentXrpPriceUsd 
                : new BigDecimal("0.85");

        // Calculate expected USDT0 output: amountIn * xrpPrice (USDT0 has 6 decimals, FXRP has 18)
        BigDecimal expectedAmountOut = amountIn.multiply(xrpPrice).setScale(6, RoundingMode.HALF_DOWN);

        // Calculate minimumAmountOut = expectedAmountOut * (1 - slippage / 100)
        BigDecimal slippageFactor = BigDecimal.ONE.subtract(slippage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_DOWN));
        BigDecimal minimumAmountOut = expectedAmountOut.multiply(slippageFactor).setScale(6, RoundingMode.HALF_DOWN);

        // Price Impact Estimation (e.g. 0.05% per 10,000 FXRP)
        BigDecimal priceImpact = amountIn.divide(new BigDecimal("100000"), 4, RoundingMode.HALF_DOWN).multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_DOWN);

        List<String> route = Arrays.asList(FXRP_ADDRESS, USDT0_ADDRESS);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("status", "ACTIVE");
        response.put("amountIn", amountIn.setScale(4, RoundingMode.HALF_DOWN).toString());
        response.put("assetIn", "FXRP");
        response.put("assetOut", "USDT0");
        response.put("expectedAmountOut", expectedAmountOut.toString());
        response.put("minimumAmountOut", minimumAmountOut.toString());
        response.put("slippagePercent", slippage.setScale(2, RoundingMode.HALF_DOWN).toString() + "%");
        response.put("priceImpactPercent", priceImpact.toString() + "%");
        response.put("route", route);
        response.put("routerAddress", COSTON2_ROUTER_ADDRESS);
        response.put("quoteTimestamp", timestamp);
        response.put("validUntilTimestamp", timestamp + QUOTE_VALIDITY_SECONDS);
        response.put("deadline", deadline);

        return response;
    }

    public boolean isQuoteExpired(long quoteTimestamp) {
        long current = Instant.now().getEpochSecond();
        return (current - quoteTimestamp) > QUOTE_VALIDITY_SECONDS;
    }
}
