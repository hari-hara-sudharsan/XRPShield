-- ===========================================================
-- XRPShield Database Schema — Migration V4 (Blockchain Infrastructure Metadata)
-- Target DB: Supabase PostgreSQL 15+
-- ===========================================================

-- 1. Contract Metadata Table
CREATE TABLE IF NOT EXISTS contract_metadata (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    contract_name VARCHAR(100) NOT NULL,
    contract_address VARCHAR(64) NOT NULL UNIQUE,
    abi_json TEXT,
    network_name VARCHAR(50) NOT NULL DEFAULT 'Flare Coston2',
    chain_id BIGINT NOT NULL DEFAULT 114,
    deployed_block BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_contract_meta_address ON contract_metadata(contract_address);
CREATE INDEX IF NOT EXISTS idx_contract_meta_name ON contract_metadata(contract_name);

-- 2. Deployment History Table
CREATE TABLE IF NOT EXISTS deployment_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    contract_name VARCHAR(100) NOT NULL,
    contract_address VARCHAR(64) NOT NULL,
    tx_hash VARCHAR(66) UNIQUE,
    deployer_address VARCHAR(64) NOT NULL,
    gas_used BIGINT,
    network VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_deploy_history_tx ON deployment_history(tx_hash);

-- 3. Network Configurations Table
CREATE TABLE IF NOT EXISTS network_configurations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    network_name VARCHAR(50) NOT NULL UNIQUE,
    chain_id BIGINT NOT NULL UNIQUE,
    rpc_url VARCHAR(255) NOT NULL,
    explorer_url VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- 4. Blockchain Transaction Logs Table
CREATE TABLE IF NOT EXISTS blockchain_transaction_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tx_hash VARCHAR(66) NOT NULL UNIQUE,
    from_address VARCHAR(64) NOT NULL,
    to_address VARCHAR(64),
    block_number BIGINT,
    gas_price NUMERIC(38, 0),
    gas_used BIGINT,
    status VARCHAR(20) NOT NULL, -- SUCCESS, FAILED
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tx_logs_hash ON blockchain_transaction_logs(tx_hash);

-- 5. Blockchain Event Logs Table
CREATE TABLE IF NOT EXISTS blockchain_event_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_name VARCHAR(100) NOT NULL,
    contract_address VARCHAR(64) NOT NULL,
    tx_hash VARCHAR(66) NOT NULL,
    block_number BIGINT NOT NULL,
    log_index INT NOT NULL,
    event_data TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_event_logs_event ON blockchain_event_logs(event_name);
CREATE INDEX IF NOT EXISTS idx_event_logs_tx ON blockchain_event_logs(tx_hash);
