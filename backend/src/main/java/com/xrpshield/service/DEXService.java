package com.xrpshield.service;

import com.xrpshield.dto.DEXQuoteDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DEXService {

    private final FtsoService ftsoService;

    public DEXService(FtsoService ftsoService) {
        this.ftsoService = ftsoService;
    }

    public DEXQuoteDto getDEXQuote(BigDecimal amountInFXRP, BigDecimal slippagePercent) {
        if (amountInFXRP == null || amountInFXRP.compareTo(BigDecimal.ZERO) <= 0) {
            amountInFXRP = new BigDecimal("100");
        }
        if (slippagePercent == null || slippagePercent.compareTo(BigDecimal.ZERO) < 0) {
            slippagePercent = new BigDecimal("0.5"); // Default 0.5%
        }

        BigDecimal liveXRPPrice = ftsoService.fetchLiveXRPUSDPrice().getPrice();
        BigDecimal expectedUsdtOut = amountInFXRP.multiply(liveXRPPrice).setScale(6, RoundingMode.HALF_UP);

        BigDecimal slippageMultiplier = BigDecimal.ONE.subtract(slippagePercent.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP));
        BigDecimal minUsdtOut = expectedUsdtOut.multiply(slippageMultiplier).setScale(6, RoundingMode.HALF_UP);

        Long deadline = (System.currentTimeMillis() / 1000) + 1200; // 20 mins deadline

        return new DEXQuoteDto(
            "FXRP / USD₮0",
            amountInFXRP,
            expectedUsdtOut,
            minUsdtOut,
            slippagePercent,
            liveXRPPrice,
            "0xe3A1b355ca63abCBC9589334B5e609583C7BAa06", // Coston2 BlazeSwap Router
            "0x5bb8082987515f40398fb9893d90616b47c04208", // FXRP Token
            "0x0000000000000000000000000000000000000000", // USDT0 Token
            deadline,
            "145,000 gas (~0.00015 FLR)"
        );
    }
}
