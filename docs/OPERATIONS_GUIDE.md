# XRPShield — Operations, Monitoring & Disaster Recovery Guide

## 1. Executive Operations Summary
This guide details the operational procedures, alert severity matrix, database backup strategies, deployment rollback plans, and health monitoring for **XRPShield**.

---

## 2. Subsystem Health Probes & Monitoring

| Component | Endpoint / Probe | Expected Latency | Threshold Alert |
|---|---|---|---|
| Spring Boot Backend | `/api/v1/platform/status` | < 20 ms | CRITICAL if > 500ms or 500 errors |
| Supabase PostgreSQL | `HikariCP Pool Check` | < 10 ms | WARNING if pool usage > 80% |
| Flare Coston2 RPC | `eth_blockNumber` | < 100 ms | WARNING if block height stalls > 60s |
| FCC TEE Enclave | `/v1/attestation/quote` | < 150 ms | CRITICAL if hardware quote invalid |
| OpenAI API Adapter | `/v1/chat/completions` | < 500 ms | INFO if rate-limited (circuit breaker) |

---

## 3. Disaster Recovery & Rollback Strategy

### Database Backup
- Daily automated pg_dump snapshot retained for 30 days.
- Point-in-time recovery enabled on Supabase PostgreSQL instance.

### Deployment Rollback Plan
1. Revert Spring Boot backend jar to previous tagged version release.
2. If database migration failure occurs, run Flyway / SQL rollback script.
3. Verify contract immutability on Flare Coston2 explorer.
