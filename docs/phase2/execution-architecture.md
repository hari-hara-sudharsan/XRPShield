# XRPShield Phase 2 — 10-Stage Execution State Machine Architecture

---

## 📜 1. End-to-End Execution Flowchart

```text
 ┌──────────────┐     ┌─────────────────────┐     ┌──────────────────────┐
 │ USER DEPOSIT │────►│ CONFIDENTIAL POLICY │────►│  KECCAK256 COMMIT    │
 │ (100k FXRP)  │     │ (triggerDrop: 10%)  │     │  (Registered Vault)  │
 └──────────────┘     └─────────────────────┘     └──────────┬───────────┘
                                                             │
                                                             ▼
 ┌──────────────┐     ┌─────────────────────┐     ┌──────────────────────┐
 │ ON-CHAIN SWAP│◄────│ EIP-712 ATTESATION  │◄────│ TEE ENCLAVE EVAL     │
 │ (FXRP->USDT0)│     │ (Signer Recovered)  │     │ (FTSOv2 Price Check) │
 └──────┬───────┘     └─────────────────────┘     └──────────────────────┘
        │
        ▼
 ┌──────────────┐     ┌─────────────────────┐
 │ COSTON2 TX   │────►│ VERIFIABLE PROOF    │
 │ (Block Receipts)   │ (Explorer Links)    │
 └──────────────┘     └─────────────────────┘
```

---

## ⚙️ 2. 10-Stage State Machine Lifecycle

1. **`READY`**: Vault is active with funded FXRP balance (`100,000 FXRP`).
2. **`FCC_REQUESTED`**: Instruction `0x585250...001` dispatched to `TeeExtensionRegistry`.
3. **`FCC_PROCESSING`**: ECIES payload decrypted inside isolated TEE hardware RAM.
4. **`FCC_APPROVED`**: Private trigger evaluation passes (`priceDrop >= 10.0%`).
5. **`QUOTE_REQUESTED`**: `DexQuoteService` queries DEX router for live `getAmountsOut`.
6. **`QUOTE_VALIDATED`**: Slippage check confirms output $\ge \text{minimumAmountOut}$.
7. **`EXECUTION_AUTHORIZED`**: Smart contract state transitions to `TEE_APPROVED`.
8. **`TRANSACTION_SUBMITTED`**: `executeHedge` transaction submitted to Coston2 RPC.
9. **`TRANSACTION_CONFIRMED`**: Block receipt status `#1` returned with log topics.
10. **`SETTLED`**: Vault custody balance updated (`+8.4575 USDT0`).
