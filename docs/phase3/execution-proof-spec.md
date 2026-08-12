# XRPShield Phase 3 Sprint 3 — Cryptographic Execution Proof Specification

---

## 📜 1. ExecutionProof Cryptographic Chain

XRPShield constructs an immutable cryptographic proof chain linking policy creation directly to Coston2 token settlement:

$$\text{Policy} \xrightarrow{\text{Keccak256}} \text{PolicyCommitment} \xrightarrow{\text{InstructionSender}} \text{InstructionID} \xrightarrow{\text{EIP-712 Signature}} \text{ActionResult} \xrightarrow{\text{SparkDEX Quote}} \text{QuoteHash} \xrightarrow{\text{EVM Swap}} \text{TxHash}$$

---

## 🏗️ 2. ExecutionProof JSON Schema

```json
{
  "proofVersion": 1,
  "chainId": 114,
  "vaultAddress": "0xb7902ebdce1d31ddcef6e7f789c1a5611186e8a9",
  "instructionId": "0x585250536869656c64464343457874656e73696f6e0000000000000000000001",
  "policyCommitment": "0x8f3c71a9e29a3b610c4f8d5b1c7e9a8f2e4c1b0d3a5e7f9c2b4a6d8e0f2c4a6b",
  "policyVersion": 1,
  "decision": "APPROVED",
  "approvedHedgeAmountWei": "10000000000000000000",
  "quoteHash": "0x3a5e7f9c2b4a6d8e0f2c4a6b8f3c71a9e29a3b610c4f8d5b1c7e9a8f2e4c1b0d",
  "marketDataHash": "0x7f9c2b4a6d8e0f2c4a6b8f3c71a9e29a3b610c4f8d5b1c7e9a8f2e4c1b0d3a5e",
  "actionResultHash": "0x2b4a6d8e0f2c4a6b8f3c71a9e29a3b610c4f8d5b1c7e9a8f2e4c1b0d3a5e7f9c",
  "attestationSigner": "0x70997970C51812dc3A010C7d01b50e0d17dc79C8",
  "transactionHash": "0x3fe85c1668067f91274cab7b46800bd59fe11375eacb1abfe9b5a4e778447cb3",
  "blockNumber": 33973480,
  "settlementStatus": "CONFIRMED",
  "timestamp": 1786588800
}
```

---

## 💾 3. Proof Download & Export Capabilities
Users and hackathon judges can download raw proof files via:
- REST Endpoint: `GET /api/v1/proof/{instructionId}/export`
- Frontend UI: Click **[Download Execution Proof JSON]** button on `/proof.html`.
