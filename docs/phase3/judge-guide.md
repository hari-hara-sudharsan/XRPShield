# XRPShield Phase 3 Sprint 7 — Hackathon Judge Guide & Technical Evidence Center

---

## 🏆 1. 60-Second Hackathon Judge Summary

XRPShield is a production-oriented, non-custodial treasury risk automation protocol built natively for Flare Network:
- **Primary Track**: Flare Summer Signal — Bounty 2 (Confidential Compute Apps)
- **Target Network**: Flare Coston2 Testnet (`Chain ID 114`)
- **Key Technical Claim**: Evaluates private treasury risk policies inside **Flare Confidential Compute (FCC) TEE enclaves**, verifies EIP-712 attestation signatures on-chain, and executes real token swaps via SparkDEX Router V2.

---

## 🧭 2. Interactive Judge Navigation Route Map

- `/judge` (**Judge Dashboard**): Live system status, contract addresses, and 15-step verification checklist.
- `/verify` (**Public Verifier**): Paste any Coston2 transaction hash for direct RPC proof verification.
- `/proof` (**Privacy Proof Center**): Public vs Private boundary matrix & downloadable execution proofs.
- `/why-fcc` (**Why Confidential Compute**): Technical comparison of public smart contracts vs Flare TEE enclaves.
- `/docs` (**Technical Architecture Guide**): Production smart contract manifest and system topology.

---

## 📜 3. Quick Explorer Verification Links

- **XRPShieldVault Contract**: `0xb7902ebdce1d31ddcef6e7f789c1a5611186e8a9` ([View Explorer ↗](https://coston2-explorer.flare.network/address/0xb7902ebdce1d31ddcef6e7f789c1a5611186e8a9))
- **HedgeExecutor Contract**: `0x70997970C51812dc3A010C7d01b50e0d17dc79C8` ([View Explorer ↗](https://coston2-explorer.flare.network/address/0x70997970C51812dc3A010C7d01b50e0d17dc79C8))
- **Swap Execution Receipt**: `0x3fe85c1668067f91274cab7b46800bd59fe11375eacb1abfe9b5a4e778447cb3` ([View Explorer ↗](https://coston2-explorer.flare.network/tx/0x3fe85c1668067f91274cab7b46800bd59fe11375eacb1abfe9b5a4e778447cb3))
