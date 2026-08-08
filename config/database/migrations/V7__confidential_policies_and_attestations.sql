-- ===========================================================
-- XRPShield Database Schema — Migration V7 (Confidential Policies & TEE Attestations)
-- Target DB: Supabase PostgreSQL 15+
-- ===========================================================

-- 1. Confidential Policies Table
CREATE TABLE IF NOT EXISTS confidential_policies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vault_id UUID NOT NULL REFERENCES vaults(id) ON DELETE CASCADE,
    policy_name VARCHAR(100) NOT NULL,
    policy_version INT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, PAUSED, DEACTIVATED
    public_metadata TEXT,
    policy_hash VARCHAR(66) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_policies_vault ON confidential_policies(vault_id);
CREATE INDEX IF NOT EXISTS idx_policies_hash ON confidential_policies(policy_hash);

-- 2. Encrypted Policies Table (AES-256-GCM Encrypted Parameters)
CREATE TABLE IF NOT EXISTS encrypted_policies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    policy_id UUID NOT NULL UNIQUE REFERENCES confidential_policies(id) ON DELETE CASCADE,
    encrypted_payload TEXT NOT NULL,
    iv VARCHAR(64) NOT NULL,
    auth_tag VARCHAR(64) NOT NULL,
    key_id VARCHAR(100) NOT NULL DEFAULT 'SYSTEM_MASTER_KMS_V1',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Policy Version History Table
CREATE TABLE IF NOT EXISTS policy_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    policy_id UUID NOT NULL REFERENCES confidential_policies(id) ON DELETE CASCADE,
    version INT NOT NULL,
    changes_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_policy_hist_policy ON policy_history(policy_id);

-- 4. Policy Evaluations Table
CREATE TABLE IF NOT EXISTS policy_evaluations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    policy_id UUID NOT NULL REFERENCES confidential_policies(id) ON DELETE CASCADE,
    vault_id UUID NOT NULL REFERENCES vaults(id) ON DELETE CASCADE,
    evaluation_status VARCHAR(30) NOT NULL, -- COMPLIANT, BREACHED, ERROR
    result_summary TEXT,
    evaluated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_evaluations_policy ON policy_evaluations(policy_id);

-- 5. Policy Attestations Table (Flare TEE Proofs)
CREATE TABLE IF NOT EXISTS policy_attestations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    policy_id UUID NOT NULL REFERENCES confidential_policies(id) ON DELETE CASCADE,
    attestation_id VARCHAR(100) NOT NULL UNIQUE,
    enclave_quote_hash VARCHAR(66) NOT NULL,
    verification_status VARCHAR(20) NOT NULL DEFAULT 'VERIFIED', -- VERIFIED, UNVERIFIED, FAILED
    attested_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_attestations_policy ON policy_attestations(policy_id);

-- 6. Policy Execution Queue Table
CREATE TABLE IF NOT EXISTS policy_execution_queue (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    policy_id UUID NOT NULL REFERENCES confidential_policies(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, SUBMITTED, COMPLETED, FAILED
    scheduled_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    executed_at TIMESTAMP WITH TIME ZONE
);
