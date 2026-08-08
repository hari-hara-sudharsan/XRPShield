-- ===========================================================
-- XRPShield Database Schema — Migration V5 (System Integration & Feature Flags)
-- Target DB: Supabase PostgreSQL 15+
-- ===========================================================

CREATE TABLE IF NOT EXISTS feature_flags (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    flag_key VARCHAR(100) NOT NULL UNIQUE,
    flag_name VARCHAR(150) NOT NULL,
    description TEXT,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_feature_flags_key ON feature_flags(flag_key);

INSERT INTO feature_flags (flag_key, flag_name, description, is_enabled)
VALUES
    ('FEATURE_AUTH_BCRYPT', 'BCrypt Password Authentication', 'Enable BCrypt password authentication pipeline', true),
    ('FEATURE_AUTH_METAMASK', 'MetaMask Web3 Auth', 'Enable EIP-191 MetaMask wallet signature verification', true),
    ('FEATURE_BLOCKCHAIN_SYNC', 'Flare Blockchain Sync', 'Enable scheduled Web3j RPC block and event synchronization', true),
    ('FEATURE_AUDIT_LOGGING', 'System Audit Trail', 'Enable persistent security audit logging', true)
ON CONFLICT (flag_key) DO NOTHING;

CREATE TABLE IF NOT EXISTS system_health_metrics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    subsystem_name VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    latency_ms BIGINT,
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_health_metrics_subsystem ON system_health_metrics(subsystem_name);
