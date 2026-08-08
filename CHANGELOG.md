# Changelog

All notable changes to the **XRPShield** platform will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-08-06

### Added
- **Core Infrastructure & Authentication:** Spring Boot 3 Java 21 LTS backend, Supabase PostgreSQL V1-V11 migrations, MetaMask EIP-191 Web3 signature authentication, HMAC-SHA512 JWT tokens.
- **Smart Contracts (Solidity 0.8.24):** `VaultManager.sol`, `AccessManager.sol`, `TreasuryStorage.sol`, `XRPShieldHealth.sol` on Flare Coston2 Testnet.
- **Flare Confidential Compute (FCC):** AES-256-GCM symmetric policy payload encryption, TEE enclave attestation proof logging (`FCCClient`, `FCCAdapter`, `AttestationService`).
- **Treasury Decision Engine:** Versioned decision generator (`NO_ACTION`, `PROTECT_POSITION`, `REDUCE_EXPOSURE`, `INCREASE_PROTECTION`, `REQUEST_REVIEW`, `EMERGENCY_EXIT`), background queue scheduler (`DecisionScheduler.java`).
- **Protected Treasury Execution Engine:** Protected on-chain execution tracking, state machine management (`PENDING` -> `COMPLETED`), gateway receipt confirmation (`ExecutionGateway.java`).
- **Treasury Intelligence Layer:** OpenAI GPT-4o integration adapter (`PolicyAssistantService.java`), Privacy Filter Guard (`PromptBuilder.java`), plain-language decision explanations, executive report generator.
- **Platform Observability & Resilience:** Subsystem health probes (`PlatformHealthIndicator.java`), system metrics collector (`PlatformMetricsCollector.java`), circuit breakers (`CircuitBreakerManager.java`), alert dispatcher (`AlertNotificationService.java`).
- **SaaS UI/UX Experience:** Modern dark glassmorphic design system (`design-system.css`), redesigned SaaS Treasury Dashboard (`pages/dashboard.html`), notification center drawer widget, global search, settings page (`pages/settings.html`).
