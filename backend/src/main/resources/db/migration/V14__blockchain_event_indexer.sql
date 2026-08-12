-- Migration V14: Real Blockchain Event Indexer Table with Idempotency Constraint

CREATE TABLE IF NOT EXISTS blockchain_event_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_hash VARCHAR(66) NOT NULL,
    block_number BIGINT NOT NULL,
    log_index INT NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    wallet_address VARCHAR(42),
    vault_id VARCHAR(42),
    event_timestamp TIMESTAMP NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'CONFIRMED',
    raw_payload TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_tx_hash_log_index UNIQUE (transaction_hash, log_index)
);

CREATE INDEX IF NOT EXISTS idx_blockchain_events_wallet ON blockchain_event_logs(wallet_address);
CREATE INDEX IF NOT EXISTS idx_blockchain_events_vault ON blockchain_event_logs(vault_id);
CREATE INDEX IF NOT EXISTS idx_blockchain_events_type ON blockchain_event_logs(event_type);
