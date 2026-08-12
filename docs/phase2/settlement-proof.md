# XRPShield Phase 2 — Settlement Proof Schema & Explorer Link Specification

---

## 📜 1. Settlement Proof JSON Schema

Upon completion of on-chain hedge execution, XRPShield assembles an immutable proof payload:

```json
{
  "vaultId": "0xb7902ebdce1d31ddcef6e7f789c1a5611186e8a9",
  "policyCommitment": "0x8f3c71a9e29a3b610c4f8d5b1c7e9a8f2e4c1b0d3a5e7f9c2b4a6d8e0f2c4a6b",
  "instructionId": "0x585250536869656c64464343457874656e73696f6e0000000000000000000001",
  "decision": "APPROVED",
  "attestationSigner": "0x70997970C51812dc3A010C7d01b50e0d17dc79C8",
  "dexRouter": "0x600109D9cDe3267E1408892f39C27DBdf8dd6B4B",
  "inputAsset": "FXRP",
  "inputAmount": "10.00",
  "outputAsset": "USDT0",
  "outputAmount": "8.4575",
  "transactionHash": "0x3fe85c1668067f91274cab7b46800bd59fe11375eacb1abfe9b5a4e778447cb3",
  "blockNumber": 33973480,
  "status": "VERIFIED_ON_CHAIN",
  "explorerUrl": "https://coston2-explorer.flare.network/tx/0x3fe85c1668067f91274cab7b46800bd59fe11375eacb1abfe9b5a4e778447cb3"
}
```

---

## 🔗 2. Explorer Verification Standards

Every public proof entry rendered in `/proof` or `/verification` must provide direct clickable markdown and HTML links to the Flare Coston2 Block Explorer:
- `https://coston2-explorer.flare.network/tx/{txHash}`
- `https://coston2-explorer.flare.network/address/{contractAddress}`
