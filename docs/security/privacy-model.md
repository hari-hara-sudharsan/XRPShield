# XRPShield Phase 4 — Confidential Policy Privacy Model & Data Audit Report

**Date**: August 12, 2026  
**Target Architecture**: Flare Confidential Compute (FCC) + ECIES Authenticated Encryption + TEE Hardware Enclave  
**Scope**: Confidential Policy Protection, Data Trace Pipeline, On-Chain Event Leakage Audit, and Data Taxonomy.

---

## 1. Sensitive Data Taxonomy & Classification

XRPShield classifies data into 6 strict privacy domains to ensure user financial risk strategies remain confidential:

| Data Category | Parameters Included | Storage / Memory Location | Visibility |
|---|---|---|---|
| **Private Strategy Data** | Trigger Threshold (-15%), Hedge Ratio (100%), Max Risk Limit, Treasury Strategy | Client Browser Memory / ECIES Encrypted Envelope | Strictly Private to User |
| **TEE-Only Data** | Decrypted Policy Struct, FTSOv2 Delta Math, Enclave Calculation State | TEE Enclave Hardware Isolated Memory | Hardware TEE Memory Only |
| **Public On-Chain Data** | Vault ID, Policy Commitment Hash, Instruction ID, Approved Hedge Cap, Status | `XRPShieldVault.sol` EVM State & Events | Public Blockchain |
| **Backend Advisory Data** | Obfuscated Vault ID, Public Wallet Address, Anonymous Settings | Spring Boot Memory / In-Memory Indexer | Internal API Only |
| **Database Data** | Vault Alias, Notification Webhooks, Historical Event Logs | Supabase Relational Tables | User-Authenticated RLS |
| **ActionResult Data** | Status (`APPROVED`), Approved Hedge Amount (`10,000 FXRP`), EIP-712 Signature | Calldata submitted to `submitPolicyEvaluationResult()` | Public Verification Payload |

---

## 2. End-to-End Data Trace Pipeline

```text
 ┌────────────────────────────────────────────────────────────────────────┐
 │                      DATA TRACE PRIVACY LIFECYCLE                      │
 ├───────────────────┬───────────────────────────────────┬────────────────┤
 │ Stage             │ Data Format                       │ Plaintext Leak │
 ├───────────────────┼───────────────────────────────────┼────────────────┤
 │ 1. Frontend UI    │ User Inputs (-15% Drop, 100%)     │ Client Browser │
 │ 2. Encryption     │ ECIES Secp256k1 Ciphertext        │ NONE           │
 │ 3. Instruction    │ Encrypted Payload + Policy Hash    │ NONE           │
 │ 4. FCC Routing    │ Encrypted Payload                 │ NONE           │
 │ 5. TEE Enclave    │ Decrypted inside isolated memory  │ TEE RAM ONLY   │
 │ 6. ActionResult   │ Minimal Attestation Payload       │ Minimal Cap    │
 │ 7. Blockchain     │ EIP-712 Signature & Event Log     │ NONE           │
 └───────────────────┴───────────────────────────────────┴────────────────┘
```

### Trace Step Details:
1. **Frontend Creation**: User enters `-15% drop threshold` and `100% hedge ratio`. Plaintext exists temporarily in local browser component state.
2. **ECIES Encryption**: Client encrypts payload using TEE Enclave Public Key (`0x04a1b2...`). Plaintext is immediately garbage collected from JS scope.
3. **Instruction Dispatch**: `requestPolicyEvaluation()` receives `policyCommitment` (`keccak256(encryptedPayload)`). Neither calldata nor transaction payload contains threshold percentages.
4. **Flare FCC Routing**: `TeeExtensionRegistry` routes encrypted bytes. Relay nodes see only opaque ciphertext.
5. **TEE Enclave Processing**: Enclave decrypts payload inside hardware-isolated memory (Intel SGX / AMD SEV). Evaluates FTSOv2 oracle price delta without emitting internal variables.
6. **ActionResult Attestation**: Enclave constructs `ActionResult` containing minimal fields required for authorization (`status: "APPROVED"`, `approvedHedgeAmount: 10000 FXRP`, `attestationHash`). The `-15%` rule is NEVER included in the result.
7. **On-Chain Verification**: `XRPShieldVault.sol` verifies signature and updates `InstructionRecord`. Public blockchain state records ONLY that instruction `0x5852...` was approved for up to `10,000 FXRP`.

---

## 3. Privacy Leakage Audit Matrix

| Audit Target | Search Query / Inspection Method | Plaintext Parameters Found? | Defense Mechanism |
|---|---|---|---|
| **Browser Console** | Inspected `console.log` during policy submit | ❌ NONE | Debug logs sanitized in production JS builds |
| **Network Payload** | Inspected `POST /api/policy/commit` REST requests | ❌ NONE | Requests transmit ECIES ciphertext & commitment hash only |
| **Spring Boot Logs** | Grepped backend app logs for threshold values | ❌ NONE | Service layers operate on hash digests |
| **Supabase DB** | Queried `vault_policies` table schema | ❌ NONE | Database stores policy hash and encrypted payload blob |
| **Calldata** | Decoded `requestPolicyEvaluation` & `executeHedge` txs | ❌ NONE | Inputs take `bytes32 policyCommitment` and `uint256 amountIn` |
| **Contract Events**| Inspected `PolicyEvaluationRequested` & `HedgeExecuted` | ❌ NONE | Events emit hashes and token execution amounts only |
| **Error Messages** | Triggered invalid execution reverts | ❌ NONE | Custom errors return code enums (e.g. `InvalidDecision()`) |

---

## 4. Minimum ActionResult Principle & Policy Binding

### Minimal Output Enforcement:
The TEE `ActionResult` returns ONLY the quantitative cap (`approvedHedgeAmount`) needed by `HedgeExecutor.sol` to validate DEX swap bounds. Confidential strategy parameters (such as trailing stop percentages, volatility multipliers, or target asset allocations) NEVER leave enclave RAM.

### Cryptographic Policy Binding:
`XRPShieldVault.sol` enforces that `verifiedAttestations[instructionId].policyCommitment == params.policyCommitment`. Submitting an attestation generated for policy $A$ against execution request for policy $B$ strictly reverts `InvalidPolicyCommitment()`.

---

## 5. Conclusion & Verification

An external third party monitoring Flare Coston2 EVM transactions can observe:
- That a vault executed a swap of `10 FXRP` for `8.4575 USDT0`.
- That instruction `0x585250...` was authorized by TEE signer `0x709979...`.

**The third party CANNOT derive:**
- The user's underlying risk trigger threshold (e.g. whether the hedge was triggered at -5%, -15%, or -30%).
- The user's secret hedge ratio strategy.
- The confidential treasury policy conditions.
