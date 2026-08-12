# XRPShield Phase 1 — Canonical Policy Commitment & Versioning Specification

---

## 📜 1. Canonical Policy Encoding

To guarantee deterministic commitment matching between client submissions and TEE evaluations, policy payloads are encoded into canonical JSON before hashing:

```json
{
  "vaultAddress": "0xb7902ebdce1d31ddcef6e7f789c1a5611186e8a9",
  "asset": "FXRP",
  "hedgeRatio": "1.0000",
  "triggerThreshold": "10.0",
  "maximumProtection": "100000.0",
  "deadline": 1786588800,
  "nonce": 1,
  "policyVersion": 1
}
```

---

## 🔑 2. Policy Commitment Hashing Formula

The canonical policy commitment is computed deterministically via Keccak256:

$$\text{policyCommitment} = \text{keccak256}(\text{UTF8}(\text{canonicalJSON}))$$

### Invariants:
1. **Determinism**: The same policy parameters ALWAYS generate the exact same 32-byte `bytes32` commitment hash.
2. **Immutability**: Changing a single field (e.g. `triggerThreshold` from `10.0` to `10.1`) alters the commitment digest completely.
3. **Cross-Vault Isolation**: `vaultAddress` is bound to the payload; a policy for Vault A CANNOT be applied to Vault B.
4. **Versioning Isolation**: Policy Version 1 (`policyVersion = 1`) commitments CANNOT be satisfied by ActionResults generated for Policy Version 2.
