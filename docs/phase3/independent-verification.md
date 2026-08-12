# XRPShield Phase 3 Sprint 4 — Independent Verification Engine & Verification Hub

---

## 🔍 1. Zero Backend Trust Architecture

The `ProofVerifier` engine operates without trusting Supabase, Spring Boot, or frontend state. It queries the Flare Coston2 EVM RPC directly to reconstruct execution receipts:

```text
  User Input (Tx Hash or Proof JSON)
              │
              ▼
  Direct Coston2 RPC Query (eth_getTransactionReceipt & eth_call)
              │
              ├─► Verify Chain ID == 114
              ├─► Verify Vault Contract Bytecode
              ├─► Verify Event Logs (HedgeExecuted)
              ├─► Recover EIP-712 Attestation Signer Address
              └─► Validate Token Transfer Amounts
              │
              ▼
  Verification Status Output: VALID / INVALID
```

---

## 🌐 2. Public Verification Portal (`/verify.html`)

Users and judges can paste any transaction hash (e.g. `0x3fe85c1668067f91274cab7b46800bd59fe11375eacb1abfe9b5a4e778447cb3`) to receive an instant verification output:

```json
{
  "verificationStatus": "VALID",
  "chainIdVerified": true,
  "vaultVerified": true,
  "attestationSignatureVerified": true,
  "quoteBoundVerified": true,
  "transactionConfirmed": true,
  "tokenTransferVerified": true,
  "settlementVerified": true
}
```
