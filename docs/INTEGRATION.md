# XRPShield — Application Integration Architecture

## 1. Overview
The XRPShield Application Integration Layer connects all completed subsystems (Database, Authentication, Web3 MetaMask Wallet Auth, Web3j RPC Blockchain Client, and Solidity Smart Contracts) through clean, decoupled Service Boundaries.

---

## 2. Gateway & Facade Pattern Architecture

```
                                  +-----------------------+
                                  |   ApplicationFacade   |
                                  +-----------+-----------+
                                              |
                   +--------------------------+--------------------------+
                   |                          |                          |
                   v                          v                          v
        +--------------------+      +--------------------+      +------------------+
        | DatabaseGateway    |      | BlockchainGateway  |      | WalletGateway    |
        +---------+----------+      +---------+----------+      +--------+---------+
                  |                           |                          |
                  v                           v                          v
        +--------------------+      +--------------------+      +------------------+
        | Supabase Postgres  |      | Web3j Flare RPC    |      | MetaMask EIP-191 |
        +--------------------+      +--------------------+      +------------------+
```

### Component Roles
- **`ApplicationFacade`:** Single unified facade entry point aggregating complete system state across subsystems.
- **`BlockchainGateway`:** Encapsulates Flare RPC communication, latest block queries, gas price calculation, and deployed contract metadata.
- **`DatabaseGateway`:** Encapsulates Supabase PostgreSQL database connections, repository counts, and latency checks.
- **`WalletGateway`:** Encapsulates MetaMask EIP-191 cryptographic wallet signature verification and address validation.
- **`AuthenticationGateway`:** Encapsulates Spring Security JWT token validation and user identity management.
- **`ConfigurationService`:** Manages dynamic application feature flags stored in PostgreSQL.
- **`HealthService`:** Provides real-time aggregated health metrics for platform monitoring.

---

## 3. System Status API Reference

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/api/v1/system/status` | `GET` | Aggregated status of Database, Flare RPC, Auth, and Contracts |
| `/api/v1/system/health` | `GET` | Latency and operational state of subsystem components |
| `/api/v1/system/version` | `GET` | JDK runtime version, active profile, build version |
| `/api/v1/system/configuration` | `GET` | Dynamic feature flag toggles |
| `/api/v1/system/modules` | `GET` | Module status map across all application subsystems |
| `/api/v1/application/info` | `GET` | Unified application metadata |
