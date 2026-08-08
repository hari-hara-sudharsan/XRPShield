-- ===========================================================
-- XRPShield Database Schema — Migration V8 (Treasury Decision Engine)
-- Target DB: Supabase PostgreSQL 15+
-- ===========================================================

-- 1. Treasury Decisions Table
CREATE TABLE IF NOT EXISTS treasury_decisions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vault_id UUID NOT NULL REFERENCES vaults(id) ON DELETE CASCADE,
    policy_id UUID REFERENCES confidential_policies(id) ON DELETE SET NULL,
    decision_type VARCHAR(40) NOT NULL, -- NO_ACTION, PROTECT_POSITION, REDUCE_EXPOSURE, INCREASE_PROTECTION, REQUEST_REVIEW, EMERGENCY_EXIT
    version INT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED, EXPIRED
    rationale TEXT NOT NULL,
    attestation_id VARCHAR(100),
    decision_hash VARCHAR(66) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    db_version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_decisions_vault ON treasury_decisions(vault_id);
CREATE INDEX IF NOT EXISTS idx_decisions_type ON treasury_decisions(decision_type);
CREATE INDEX IF NOT EXISTS idx_decisions_status ON treasury_decisions(status);

-- 2. Decision History Table
CREATE TABLE IF NOT EXISTS decision_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    decision_id UUID NOT NULL REFERENCES treasury_decisions(id) ON DELETE CASCADE,
    version INT NOT NULL,
    action VARCHAR(50) NOT NULL,
    actor_address VARCHAR(64) NOT NULL,
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_decision_hist_dec ON decision_history(decision_id);

-- 3. Decision Evaluations Table
CREATE TABLE IF NOT EXISTS decision_evaluations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    decision_id UUID NOT NULL REFERENCES treasury_decisions(id) ON DELETE CASCADE,
    vault_id UUID NOT NULL REFERENCES vaults(id) ON DELETE CASCADE,
    fcc_latency_ms BIGINT NOT NULL DEFAULT 0,
    result_summary TEXT,
    evaluated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_dec_eval_dec ON decision_evaluations(decision_id);

-- 4. Decision Queue Table
CREATE TABLE IF NOT EXISTS decision_queue (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    decision_id UUID NOT NULL REFERENCES treasury_decisions(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'QUEUED', -- QUEUED, PROCESSING, COMPLETED, FAILED
    scheduled_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_dec_queue_status ON decision_queue(status);

-- 5. Decision Metadata Table
CREATE TABLE IF NOT EXISTS decision_metadata (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    decision_id UUID NOT NULL REFERENCES treasury_decisions(id) ON DELETE CASCADE,
    meta_key VARCHAR(100) NOT NULL,
    meta_value TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 6. Decision Audit Table
CREATE TABLE IF NOT EXISTS decision_audits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    decision_id UUID NOT NULL REFERENCES treasury_decisions(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL,
    actor VARCHAR(64) NOT NULL,
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
