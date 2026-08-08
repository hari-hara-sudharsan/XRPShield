-- ===========================================================
-- XRPShield Database Schema — Migration V6 (Vault Balances & Transaction Ledger)
-- Target DB: Supabase PostgreSQL 15+
-- ===========================================================

-- 1. Vault Balances Table
CREATE TABLE IF NOT EXISTS vault_balances (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vault_id UUID NOT NULL REFERENCES vaults(id) ON DELETE CASCADE,
    currency VARCHAR(20) NOT NULL DEFAULT 'FXRP',
    balance_amount NUMERIC(38, 18) NOT NULL DEFAULT 0.0,
    locked_amount NUMERIC(38, 18) NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_vault_currency UNIQUE (vault_id, currency)
);

CREATE INDEX IF NOT EXISTS idx_vault_balances_vault ON vault_balances(vault_id);

-- 2. Vault Transactions Table
CREATE TABLE IF NOT EXISTS vault_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vault_id UUID NOT NULL REFERENCES vaults(id) ON DELETE CASCADE,
    tx_type VARCHAR(20) NOT NULL, -- DEPOSIT, WITHDRAWAL, TRANSFER
    amount NUMERIC(38, 18) NOT NULL,
    currency VARCHAR(20) NOT NULL DEFAULT 'FXRP',
    tx_hash VARCHAR(66),
    from_address VARCHAR(64) NOT NULL,
    to_address VARCHAR(64) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED', -- PENDING, CONFIRMED, FAILED
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vault_tx_vault ON vault_transactions(vault_id);
CREATE INDEX IF NOT EXISTS idx_vault_tx_hash ON vault_transactions(tx_hash);

-- 3. Vault History Activity Table
CREATE TABLE IF NOT EXISTS vault_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vault_id UUID NOT NULL REFERENCES vaults(id) ON DELETE CASCADE,
    action VARCHAR(50) NOT NULL,
    actor_address VARCHAR(64) NOT NULL,
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vault_history_vault ON vault_history(vault_id);
