# XRPShield — System Architecture & Design Overview

## 1. System Vision & Objective
XRPShield is an enterprise-grade, privacy-preserving XRP Treasury & Risk Management Platform built on the Flare Network. 

Holding significant FXRP or XRP reserves exposes organizations (DAOs, treasury funds, merchants) to front-running, strategy leakage, and public transaction tracking. XRPShield bridges this gap by decoupling confidential business logic from public ledger execution.

---

## 2. Core Architectural Pillars

```
+-----------------------------------------------------------------------+
|                           User Touchpoints                            |
|             (Vanilla JS Responsive UI / Web3 Wallet Auth)             |
+-----------------------------------------------------------------------+
                                   | REST API (DTOs)
                                   v
+-----------------------------------------------------------------------+
|                    Backend Layer (Spring Boot 3 / Java 21)             |
|   - Stateless Security (JWT + Wallet Verification)                    |
|   - System Monitoring & Health Actuator                               |
|   - Audit Logging & Risk Engine Interfaces                            |
+-----------------------------------------------------------------------+
                                   |
         +-------------------------+-------------------------+
         |                                                   |
         v                                                   v
+------------------------------------+   +----------------------------------+
|      Database Storage Layer        |   |   Confidential Compute Boundary  |
|     (Supabase PostgreSQL 15+)      |   |  (Flare TEE Enclave Execution)   |
|   - Normalized Relational Tables   |   |   - Private Risk Calculations    |
|   - Audit Logs & Encrypted Metadata|   |   - Attestation Proof Generation |
+------------------------------------+   +----------------------------------+
                                                             |
                                                             v
                                         +----------------------------------+
                                         |      Flare Network Blockchain     |
                                         |    - XRPShield Smart Contracts   |
                                         |    - FXRP / FTSO Integration     |
                                         +----------------------------------+
```

### Clean Architecture & Layer Separation
1. **Presentation Layer (Frontend):** Pure Vanilla JS SPA using standard browser fetch APIs and clean component composition.
2. **Backend Application Layer (Spring Boot 3):** Java 21 service layer enforcing DTO boundaries, centralized exception handling, bean validation, and security filters.
3. **Database Layer (Supabase PostgreSQL):** Normalized relational model with SQL migrations.
4. **Confidential Execution Layer (Flare TEE):** Secure enclave sandbox where confidential algorithms and threshold parameters are calculated privately.
5. **Blockchain Layer (Flare C-Chain):** On-chain Solidity smart contracts receiving cryptographically attested execution parameters.
