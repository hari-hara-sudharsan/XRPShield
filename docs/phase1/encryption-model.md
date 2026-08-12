# XRPShield Phase 1 — Client-Side Encryption & TEE Confidentiality Model

---

## 🔒 1. Confidentiality Invariant

XRPShield guarantees that sensitive risk parameters (**trigger drop thresholds**, **hedge allocation ratios**, **maximum notional stop-loss limits**) NEVER exist in plaintext on public blockchains, frontend logs, backend databases, or network transport layers.

---

## 🔑 2. ECIES secp256k1 Encryption Pipeline

```text
 ┌───────────────────┐               ┌──────────────────────┐               ┌───────────────────┐
 │ XRPShield Client  │  ECIES Encrypt│ Encrypted Ciphertext │ Dispatch Via  │ Flare TEE Enclave │
 │ (Browser / Web3)  ├───────────────► (hex / base64)       ├───────────────► (Isolated RAM)    │
 └───────────────────┘               └──────────────────────┘               └─────────┬─────────┘
                                                                                      │ ECIES Decrypt
                                                                                      ▼
                                                                            ┌───────────────────┐
                                                                            │ Private Policy    │
                                                                            │ (trigger, ratio)  │
                                                                            └───────────────────┘
```

### Encryption Parameters
- **Curve**: `secp256k1` (EVM native curve compatibility)
- **Public Key**: Exported by TEE Enclave instance (`0x04585250...`)
- **Symmetric Cipher**: AES-128-GCM with 96-bit Initialization Vector (IV) and 128-bit Authentication Tag.
- **Key Derivation Function**: HKDF-SHA256 deriving symmetric key from ECDH shared secret.

---

## 🛡️ 3. Information Leakage Audit Matrix

| Pipeline Stage / Storage Layer | Plaintext Policy Leakage Risk | Mitigation Strategy | Status |
|---|---|---|---|
| **Browser Console / LocalStorage** | **ZERO** | Policy encrypted in Web Workers; transient memory cleared after commit. | ✅ SECURE |
| **Network Requests (HTTP/RPC)** | **ZERO** | Ciphertext payload transmitted over TLS; public params limited to `policyCommitment`. | ✅ SECURE |
| **Spring Boot Logs / Supabase** | **ZERO** | Relational DB indexes `policy_hash` and `vault_id` only. | ✅ SECURE |
| **Solidity Calldata / EVM Storage** | **ZERO** | `XRPShieldVault.sol` stores 32-byte `bytes32 policyCommitment` digest only. | ✅ SECURE |
| **TEE Enclave Hardware RAM** | **AUTHORIZED** | Isolated enclave memory (Intel SGX / AMD SEV); plaintext unreadable by host OS. | ✅ SECURE |
