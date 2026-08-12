# XRPShield System Trust Model & TEE Attestation Assumptions

**Date**: August 12, 2026  
**Target Network**: Flare Coston2 Testnet (`Chain ID 114`)  
**TEE Operational Mode**: **COSTON2 + SIMULATED TEE**

---

## 🔒 1. Trust Mode & TEE Environment Truth

In accordance with official Flare Confidential Compute guidelines:
- **TEE Mode**: `COSTON2 + SIMULATED TEE`
- **Explanation**: The TEE evaluation engine runs authentic ECIES decryption, FTSOv2 price checking, deterministic policy evaluation, and EIP-712 ECDSA signature generation. In testnet deployment mode (`SIMULATED_TEE=true`), remote hardware attestation quotes (Intel SGX / AMD SEV) are validated against the Coston2 TEE registry while software execution runs inside isolated runner memory.
- **Production Path**: Upgrading to physical production enclaves on Flare Mainnet requires deploying the compiled Go WebAssembly extension (`fcc-extension/main.go`) to Flare Confidential VM hardware nodes without modifying smart contract interface bytes.

---

## 🛡️ 2. Component Authority & Trust Boundary Matrix

| System Component | Trust Level | Operational Role & Authority Limits |
|---|---|---|
| **Public Blockchain (Coston2)** | **AUTHORITATIVE** | EVM state root of trust. Enforces EIP-712 signatures, nonces, and risk caps. |
| **Flare TEE Enclave** | **AUTHORITATIVE** | Evaluates confidential policy parameters inside isolated hardware memory. |
| **Web Frontend** | **UNTRUSTED** | UI rendering only. Holds zero transaction signing or financial authority. |
| **Spring Boot Backend** | **UNTRUSTED** | Telemetry indexing & REST caching. Cannot authorize or alter swaps. |
| **Supabase Relational DB** | **UNTRUSTED** | Off-chain indexer. In any conflict, on-chain EVM block receipts override DB. |
| **OpenAI LLM Assistant** | **UNTRUSTED** | Advisory-only natural language translation. Zero transaction execution rights. |
