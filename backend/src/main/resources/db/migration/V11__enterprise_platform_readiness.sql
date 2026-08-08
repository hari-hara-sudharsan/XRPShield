-- ===========================================================
-- XRPShield Database Schema — Migration V11 (Enterprise Platform Readiness)
-- Target DB: Supabase PostgreSQL 15+
-- ===========================================================

CREATE TABLE IF NOT EXISTS platform_notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    severity VARCHAR(20) NOT NULL DEFAULT 'INFO',
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_plat_notif_user ON platform_notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_plat_notif_sev ON platform_notifications(severity);

CREATE TABLE IF NOT EXISTS system_metrics_snapshots (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    metric_name VARCHAR(100) NOT NULL,
    metric_value DOUBLE PRECISION NOT NULL,
    category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sys_met_name ON system_metrics_snapshots(metric_name);
CREATE INDEX IF NOT EXISTS idx_sys_met_cat ON system_metrics_snapshots(category);
