package com.xrpshield.dto;

import java.math.BigDecimal;

public class MarketPriceResponseDto {
    private BigDecimal price;
    private Long rawPrice;
    private Integer decimals;
    private Long timestamp;
    private String feedId;
    private String source;
    private Boolean stale;
    private Long freshnessSeconds;

    public MarketPriceResponseDto() {}

    public MarketPriceResponseDto(BigDecimal price, Long rawPrice, Integer decimals, Long timestamp, String feedId, String source, Boolean stale, Long freshnessSeconds) {
        this.price = price;
        this.rawPrice = rawPrice;
        this.decimals = decimals;
        this.timestamp = timestamp;
        this.feedId = feedId;
        this.source = source;
        this.stale = stale;
        this.freshnessSeconds = freshnessSeconds;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getRawPrice() {
        return rawPrice;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getFeedId() {
        return feedId;
    }

    public String getSource() {
        return source;
    }

    public Boolean getStale() {
        return stale;
    }

    public Long getFreshnessSeconds() {
        return freshnessSeconds;
    }
}
