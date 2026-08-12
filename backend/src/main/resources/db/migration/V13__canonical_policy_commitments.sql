-- V13__canonical_policy_commitments.sql
-- Migration for canonical policy commitments and anti-replay protection

ALTER TABLE policies ADD COLUMN IF NOT EXISTS asset VARCHAR(20) DEFAULT 'FXRP';
ALTER TABLE policies ADD COLUMN IF NOT EXISTS hedge_ratio DECIMAL(10, 4) DEFAULT 1.0;
ALTER TABLE policies ADD COLUMN IF NOT EXISTS trigger_threshold DECIMAL(24, 8) DEFAULT 10.0;
ALTER TABLE policies ADD COLUMN IF NOT EXISTS maximum_protection DECIMAL(24, 8) DEFAULT 100000.0;
ALTER TABLE policies ADD COLUMN IF NOT EXISTS deadline BIGINT DEFAULT 0;
ALTER TABLE policies ADD COLUMN IF NOT EXISTS nonce BIGINT DEFAULT 1;
ALTER TABLE policies ADD COLUMN IF NOT EXISTS policy_version BIGINT DEFAULT 1;
ALTER TABLE policies ADD COLUMN IF NOT EXISTS policy_commitment VARCHAR(66);
ALTER TABLE policies ADD COLUMN IF NOT EXISTS canonical_payload TEXT;
ALTER TABLE policies ADD COLUMN IF NOT EXISTS tx_hash VARCHAR(66);
ALTER TABLE policies ADD COLUMN IF NOT EXISTS block_number BIGINT;

CREATE INDEX IF NOT EXISTS idx_policies_commitment ON policies(policy_commitment);
CREATE INDEX IF NOT EXISTS idx_policies_vault_version ON policies(vault_id, policy_version);
