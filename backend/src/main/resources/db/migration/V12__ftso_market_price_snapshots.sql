-- V12__ftso_market_price_snapshots.sql
-- Create table for storing historical Flare FTSOv2 price feed snapshots

CREATE TABLE IF NOT EXISTS market_price_snapshots (
    id VARCHAR(36) PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    price DECIMAL(24, 8) NOT NULL,
    raw_price BIGINT NOT NULL,
    decimals INT NOT NULL,
    feed_timestamp BIGINT NOT NULL,
    feed_id VARCHAR(66) NOT NULL,
    source VARCHAR(50) NOT NULL,
    stale BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_market_snapshots_symbol ON market_price_snapshots(symbol);
CREATE INDEX IF NOT EXISTS idx_market_snapshots_timestamp ON market_price_snapshots(feed_timestamp);
