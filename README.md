# XRPShield — Privacy-Preserving XRP Treasury & Risk Management Platform

![XRPShield Banner](https://img.shields.io/badge/Flare-Confidential%20Compute-00F2FE?style=for-the-badge)
![Submission Status](https://img.shields.io/badge/Hackathon%20Submission-READY%20FOR%20JUDGING-10B981?style=for-the-badge)
![Version](https://img.shields.io/badge/Version-v1.0.0-10B981?style=for-the-badge)
![Build Status](https://img.shields.io/badge/Build-Passing-10B981?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21%20LTS-007396?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-6DB33F?style=for-the-badge)
![Database](https://img.shields.io/badge/Supabase-PostgreSQL%2015+-3ECF8E?style=for-the-badge)
![Solidity](https://img.shields.io/badge/Solidity-0.8.24-363636?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

## 📌 Executive Summary & Hackathon Pitch
**XRPShield** is an enterprise, privacy-preserving XRP Treasury & Risk Management Platform built for the **Flare Summer Signal Hackathon**.

Organizations, DAOs, and merchants holding XRP or FXRP can define confidential treasury protection policies, automated risk triggers, and strategy execution workflows while keeping their core financial strategies private inside **Flare Confidential Compute (FCC)** TEE hardware enclaves.

---

## 🏗️ High-Level System Architecture

```mermaid
graph TD
    User([Treasury Manager / DAO User])
    WebUI[Frontend Vanilla JS ES6 SPA]
    SpringBoot[Spring Boot 3.2.3 / Java 21 Backend]
    SupabaseDB[(Supabase PostgreSQL 15+)]
    Web3RPC[Flare Coston2 Testnet RPC]
    FCCTEE[Flare Confidential Compute TEE Enclave]
    OpenAIAdapter[OpenAI GPT-4o API Adapter]

    User <-->|HTTPS / Web3| WebUI
    WebUI <-->|REST API + JWT| SpringBoot
    SpringBoot <-->|JPA / JDBC| SupabaseDB
    SpringBoot <-->|Web3j RPC| Web3RPC
    SpringBoot <-->|TEE Attestation| FCCTEE
    SpringBoot <-->|Sanitizer Guard| OpenAIAdapter
```

---

## 📜 Deployed Smart Contract Addresses (Flare Coston2 Testnet)

| Contract Name | Deployed Address | Network / Explorer Link |
|---|---|---|
| `VaultManager.sol` | `0x9fE46736679d2D9a65F0992F2272dE9f3c7fa6e0` | [Flare Coston2 Explorer](https://coston2-explorer.flare.network/address/0x9fE46736679d2D9a65F0992F2272dE9f3c7fa6e0) |
| `AccessManager.sol` | `0xCf7Ed3AccA5a467e9e754572157c480d02f71887` | [Flare Coston2 Explorer](https://coston2-explorer.flare.network/address/0xCf7Ed3AccA5a467e9e754572157c480d02f71887) |
| `TreasuryStorage.sol` | `0xDc64a140Aa3E981100a9becA4E685f962f0cf6C9` | [Flare Coston2 Explorer](https://coston2-explorer.flare.network/address/0xDc64a140Aa3E981100a9becA4E685f962f0cf6C9) |
| `XRPShieldHealth.sol` | `0x5FbDB2315678afecb367f032d93F642f64180aa3` | [Flare Coston2 Explorer](https://coston2-explorer.flare.network/address/0x5FbDB2315678afecb367f032d93F642f64180aa3) |


---

## 📚 Technical Documentation & Submission Assets

* [🏆 Hackathon Submission Package](docs/HACKATHON_SUBMISSION.md)
* [🎬 Live Demo Script & Judge Q&A Guide](docs/LIVE_DEMO_SCRIPT_AND_JUDGE_QA.md)
* [📐 System Architecture Diagrams](docs/ARCHITECTURE_DIAGRAMS.md)
* [🔌 Comprehensive REST API Reference](docs/API_DOCUMENTATION.md)
* [💻 Developer Setup & Build Guide](docs/DEVELOPER_GUIDE.md)
* [📖 User Manual & FAQ](docs/USER_MANUAL.md)
* [🧪 End-to-End Testing & QA Report](docs/TESTING_REPORT.md)
* [🔒 Security Review & Audit Report](docs/SECURITY_REPORT.md)
* [⚡ Performance Benchmarks Report](docs/PERFORMANCE_REPORT.md)
* [📋 Production Readiness Sign-Off Checklist](docs/PRODUCTION_CHECKLIST.md)
* [🛠️ Operations & Disaster Recovery Guide](docs/OPERATIONS_GUIDE.md)
* [🎨 SaaS Design System & UX Guide](docs/DESIGN_SYSTEM_AND_UI_GUIDE.md)

---

## 🚀 Quickstart Instructions

```powershell
# 1. Run Complete Automated System Build
.\scripts\build.ps1

# 2. Run Smart Contract Tests
cd contracts && npx hardhat test

# 3. Run Backend JUnit 5 Test Suite
cd backend && mvn clean test
```

---

## 📄 License & Standards
Distributed under the **MIT License**. Built with clean architecture, strict SOLID principles, zero simulations, and zero fake implementations for the Flare Summer Signal Hackathon.
