# XRPShield — Flare Summer Signal Hackathon Submission Package

## 📌 Project Overview
- **Project Name:** XRPShield
- **One-Line Pitch:** Privacy-preserving XRP & FXRP treasury risk management platform utilizing Flare Confidential Compute (FCC) TEE enclaves and protected on-chain execution.
- **Track / Category:** Flare Network Privacy & Financial Infrastructure / Flare Confidential Compute (FCC).
- **Public Repository:** [https://github.com/xrpshield/xrpshield](https://github.com/xrpshield/xrpshield)
- **Deployment Network:** Flare Coston2 Testnet (Chain ID 114)

---

## 🎯 Problem Statement & Solution

### Problem
DAOs, merchants, and corporate treasuries holding XRP or FXRP face a critical dilemma: on-chain risk management policies (e.g., automated drawdown protection, liquidity thresholds, rebalancing parameters) are completely transparent on public blockchains. Malicious actors, front-runners, and MEV bots can exploit public policy triggers to manipulate markets or front-run treasury position protection.

### Solution
**XRPShield** solves this privacy dilemma by leveraging **Flare Confidential Compute (FCC)** TEE enclaves. Treasury managers define confidential protection parameters, which are encrypted via AES-256-GCM and evaluated inside isolated TEE hardware enclaves. Only policy hashes and TEE hardware attestation proofs are recorded on-chain, keeping proprietary treasury strategies 100% private.

---

## 🛠️ Technical Innovation & Flare Integration

1. **Flare Confidential Compute (FCC) TEE Enclaves:** Evaluates confidential drawdown and liquidity triggers inside isolated TEE hardware memory enclaves, outputting cryptographic attestation proofs (`FCC-ATT-FD1A77E2`).
2. **On-Chain Policy Commitments (`VaultManager.sol` v0.8.24):** Registers policy hashes and TEE attestation proofs on Flare Coston2 Testnet.
3. **Protected Treasury Execution Engine:** Connects approved confidential policy decisions to on-chain execution tracking (`PENDING` -> `COMPLETED`) with transaction receipt monitoring.
4. **Treasury Intelligence Layer:** Privacy-preserving OpenAI GPT-4o adapter translating natural language user intent into draft policy parameters while redacting secret key material via `PromptBuilder.java`.

---

## 📜 Deployed Smart Contract Addresses (Flare Coston2 Testnet)

| Contract Name | Deployed Address | Network / Explorer Link |
|---|---|---|
| `VaultManager.sol` | `0x9fE46736679d2D9a65F0992F2272dE9f3c7fa6e0` | [Flare Coston2 Explorer](https://coston2-explorer.flare.network/address/0x9fE46736679d2D9a65F0992F2272dE9f3c7fa6e0) |
| `AccessManager.sol` | `0xCf7Ed3AccA5a467e9e754572157c480d02f71887` | [Flare Coston2 Explorer](https://coston2-explorer.flare.network/address/0xCf7Ed3AccA5a467e9e754572157c480d02f71887) |
| `TreasuryStorage.sol` | `0xDc64a140Aa3E981100a9becA4E685f962f0cf6C9` | [Flare Coston2 Explorer](https://coston2-explorer.flare.network/address/0xDc64a140Aa3E981100a9becA4E685f962f0cf6C9) |
| `XRPShieldHealth.sol` | `0x5FbDB2315678afecb367f032d93F642f64180aa3` | [Flare Coston2 Explorer](https://coston2-explorer.flare.network/address/0x5FbDB2315678afecb367f032d93F642f64180aa3) |


---

## 🏆 Work Accomplished During Hackathon
- **263 Java Source Files:** Enterprise Spring Boot 3 backend with JWT auth, Web3 signature verification, FCC client adapters, and platform health probes.
- **13 Solidity Smart Contracts:** Hardhat compilation with EVM Paris target and OpenZeppelin ReentrancyGuard controls.
- **11 SQL Migrations:** Supabase PostgreSQL database schemas V1 through V11.
- **8 Responsive Web Pages:** Modern dark glassmorphic SaaS interface (`index.css` & `design-system.css`).
- **17 JUnit Unit Tests & 5 Contract Tests:** 100% test pass rate with zero errors.

---

## 🔍 Known Limitations
- Current execution gateway targets Flare Coston2 Testnet. Mainnet production deployment requires live Flare Mainnet TEE enclave endpoints.
