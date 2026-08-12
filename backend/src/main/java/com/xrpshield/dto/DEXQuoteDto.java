package com.xrpshield.dto;

import java.math.BigDecimal;

public class DEXQuoteDto {
    private String pair;
    private BigDecimal amountIn;
    private BigDecimal expectedUsdtOut;
    private BigDecimal minUsdtOut;
    private BigDecimal slippagePercent;
    private BigDecimal xrpUsdPrice;
    private String dexRouterAddress;
    private String tokenInAddress;
    private String tokenOutAddress;
    private Long deadline;
    private String estimatedGas;

    public DEXQuoteDto() {}

    public DEXQuoteDto(String pair, BigDecimal amountIn, BigDecimal expectedUsdtOut, BigDecimal minUsdtOut, BigDecimal slippagePercent, BigDecimal xrpUsdPrice, String dexRouterAddress, String tokenInAddress, String tokenOutAddress, Long deadline, String estimatedGas) {
        this.pair = pair;
        this.amountIn = amountIn;
        this.expectedUsdtOut = expectedUsdtOut;
        this.minUsdtOut = minUsdtOut;
        this.slippagePercent = slippagePercent;
        this.xrpUsdPrice = xrpUsdPrice;
        this.dexRouterAddress = dexRouterAddress;
        this.tokenInAddress = tokenInAddress;
        this.tokenOutAddress = tokenOutAddress;
        this.deadline = deadline;
        this.estimatedGas = estimatedGas;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public BigDecimal getAmountIn() {
        return amountIn;
    }

    public void setAmountIn(BigDecimal amountIn) {
        this.amountIn = amountIn;
    }

    public BigDecimal getExpectedUsdtOut() {
        return expectedUsdtOut;
    }

    public void setExpectedUsdtOut(BigDecimal expectedUsdtOut) {
        this.expectedUsdtOut = expectedUsdtOut;
    }

    public BigDecimal getMinUsdtOut() {
        return minUsdtOut;
    }

    public void setMinUsdtOut(BigDecimal minUsdtOut) {
        this.minUsdtOut = minUsdtOut;
    }

    public BigDecimal getSlippagePercent() {
        return slippagePercent;
    }

    public void setSlippagePercent(BigDecimal slippagePercent) {
        this.slippagePercent = slippagePercent;
    }

    public BigDecimal getXrpUsdPrice() {
        return xrpUsdPrice;
    }

    public void setXrpUsdPrice(BigDecimal xrpUsdPrice) {
        this.xrpUsdPrice = xrpUsdPrice;
    }

    public String getDexRouterAddress() {
        return dexRouterAddress;
    }

    public void setDexRouterAddress(String dexRouterAddress) {
        this.dexRouterAddress = dexRouterAddress;
    }

    public String getTokenInAddress() {
        return tokenInAddress;
    }

    public void setTokenInAddress(String tokenInAddress) {
        this.tokenInAddress = tokenInAddress;
    }

    public String getTokenOutAddress() {
        return tokenOutAddress;
    }

    public void setTokenOutAddress(String tokenOutAddress) {
        this.tokenOutAddress = tokenOutAddress;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    public String getEstimatedGas() {
        return estimatedGas;
    }

    public void setEstimatedGas(String estimatedGas) {
        this.estimatedGas = estimatedGas;
    }
}
