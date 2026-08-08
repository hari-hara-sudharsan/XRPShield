-- ===========================================================
-- XRPShield Database Schema — Migration V9 (Protected Treasury Execution Engine)
-- Target DB: Supabase PostgreSQL 15+
-- ===========================================================

CREATE TABLE IF NOT EXISTS treasury_executions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    decision_id UUID NOT NULL REFERENCES treasury_decisions(id) ON DELETE CASCADE,
    vault_id UUID NOT NULL REFERENCES vaults(id) ON DELETE CASCADE,
    execution_state VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    tx_hash VARCHAR(66),
    block_number BIGINT,
    gas_used BIGINT,
    execution_hash VARCHAR(66) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE,
    db_version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_executions_decision ON treasury_executions(decision_id);
CREATE INDEX IF NOT EXISTS idx_executions_vault ON treasury_executions(vault_id);
CREATE INDEX IF NOT EXISTS idx_executions_state ON treasury_executions(execution_state);

CREATE TABLE IF NOT EXISTS execution_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    execution_id UUID NOT NULL REFERENCES treasury_executions(id) ON DELETE CASCADE,
    state VARCHAR(30) NOT NULL,
    actor VARCHAR(64) NOT NULL,
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_exec_hist_exec ON execution_history(execution_id);

CREATE TABLE IF NOT EXISTS execution_queue (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    execution_id UUID NOT NULL REFERENCES treasury_executions(id) ON DELETE CASCADE,
    retry_count INT NOT NULL DEFAULT 0,
    max_retries INT NOT NULL DEFAULT 3,
    status VARCHAR(20) NOT NULL DEFAULT 'QUEUED',
    scheduled_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_exec_queue_status ON execution_queue(status);

CREATE TABLE IF NOT EXISTS execution_results (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    execution_id UUID NOT NULL REFERENCES treasury_executions(id) ON DELETE CASCADE,
    result_code VARCHAR(50) NOT NULL,
    result_payload TEXT NOT NULL,
    fcc_latency_ms BIGINT NOT NULL DEFAULT 0,
    blockchain_confirmation_ms BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_exec_res_exec ON execution_results(execution_id);

CREATE TABLE IF NOT EXISTS execution_audits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    execution_id UUID NOT NULL REFERENCES treasury_executions(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL,
    actor VARCHAR(64) NOT NULL,
    tx_hash VARCHAR(66),
    wallet VARCHAR(64),
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS execution_metadata (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    execution_id UUID NOT NULL REFERENCES treasury_executions(id) ON DELETE CASCADE,
    meta_key VARCHAR(100) NOT NULL,
    meta_value TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
