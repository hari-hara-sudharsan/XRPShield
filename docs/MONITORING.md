# XRPShield — Monitoring & Scheduled Synchronization

## 1. Background Scheduled Synchronization
- **`BlockchainSyncScheduler`:** Executes every 15 seconds to poll Flare Coston2 latest block height and record Web3j RPC latency metrics.
- **`HealthMonitorScheduler`:** Executes every 30 seconds to evaluate database ping, RPC connectivity, and system operational health.

---

## 2. Structured SLF4J Request Tracing
All HTTP requests are intercepted by `RequestLoggingFilter` which injects unique correlation UUIDs, measures execution time in milliseconds, and logs structured JSON metadata:
```text
REQUEST | ID: a1b2c3d4-5678-90ab-cdef-1234567890ab | Method: GET | URI: /api/v1/system/status
RESPONSE | ID: a1b2c3d4-5678-90ab-cdef-1234567890ab | Status: 200 OK | Duration: 18ms
```
