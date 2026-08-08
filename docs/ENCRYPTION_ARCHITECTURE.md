# XRPShield — Confidential Policy Encryption Architecture

## 1. Cryptographic Standards & Decoupling Model
XRPShield separates **public metadata** (Policy Name, Version, Assigned Vault, Public Hash) from **confidential strategy parameters** (Risk Threshold, Maximum Drawdown, Stop Loss Conditions).

- **Encryption Scheme:** AES-256-GCM (`AES/GCM/NoPadding`).
- **Key Management:** `SYSTEM_MASTER_KMS_V1` with 32-byte secret keys.
- **Initialization Vector (IV):** Cryptographically random 12-byte IV per encrypted policy payload.
- **Integrity Tag:** 128-bit GCM authentication tag preventing ciphertext tampering.

---

## 2. Payload Structure

### Public Metadata (Stored Plaintext)
```json
{
  "policyName": "Primary Treasury Maximum Drawdown Policy",
  "policyVersion": 1,
  "status": "ACTIVE",
  "policyHash": "0x4d8a11bc90a..."
}
```

### Confidential Rules (Encrypted Payload)
```json
{
  "riskThreshold": 0.15,
  "maxExposure": 100000.00,
  "maxDrawdown": 0.10,
  "maxPositionSize": 50000.00,
  "stopCondition": "AUTOMATIC_DRAWDOWN_BREACH_FREEZE"
}
```
