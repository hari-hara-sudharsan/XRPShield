package com.xrpshield.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "market_price_snapshots")
public class MarketPriceSnapshotEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "symbol", length = 20, nullable = false)
    private String symbol;

    @Column(name = "price", precision = 24, scale = 8, nullable = false)
    private BigDecimal price;

    @Column(name = "raw_price", nullable = false)
    private Long rawPrice;

    @Column(name = "decimals", nullable = false)
    private Integer decimals;

    @Column(name = "feed_timestamp", nullable = false)
    private Long feedTimestamp;

    @Column(name = "feed_id", length = 66, nullable = false)
    private String feedId;

    @Column(name = "source", length = 50, nullable = false)
    private String source;

    @Column(name = "stale", nullable = false)
    private Boolean stale;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public MarketPriceSnapshotEntity() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
    }

    public MarketPriceSnapshotEntity(String symbol, BigDecimal price, Long rawPrice, Integer decimals, Long feedTimestamp, String feedId, String source, Boolean stale) {
        this();
        this.symbol = symbol;
        this.price = price;
        this.rawPrice = rawPrice;
        this.decimals = decimals;
        this.feedTimestamp = feedTimestamp;
        this.feedId = feedId;
        this.source = source;
        this.stale = stale;
    }

    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
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

    public Long getFeedTimestamp() {
        return feedTimestamp;
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

    public Instant getCreatedAt() {
        return createdAt;
    }
}
