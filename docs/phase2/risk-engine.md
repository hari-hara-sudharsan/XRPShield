# XRPShield Phase 2 — Deterministic Confidential Risk Engine Specification

---

## ⚡ 1. Deterministic Evaluation Decision Rules

The Flare Confidential Compute risk engine (`evaluator.js` / `evaluator.go`) runs purely inside TEE hardware enclaves without nondeterministic LLM calls or external network dependencies.

```text
                     ┌───────────────────────────┐
                     │ Incoming Policy & Price   │
                     └─────────────┬─────────────┘
                                   │
                                   ▼
                   ┌───────────────────────────────┐
                   │  Is Policy Deadline Expired?  │───YES───► REJECTED (POLICY_EXPIRED)
                   └───────────────┬───────────────┘
                                   │NO
                                   ▼
                   ┌───────────────────────────────┐
                   │ Is Vault Balance <= 0 or      │───YES───► REJECTED (INSUFFICIENT_BALANCE)
                   │ Vault Status != ACTIVE?       │
                   └───────────────┬───────────────┘
                                   │NO
                                   ▼
                   ┌───────────────────────────────┐
                   │ Does Candidate Hash Match     │───NO────► REJECTED (COMMITMENT_MISMATCH)
                   │ Active Policy Commitment Hash?│
                   └───────────────┬───────────────┘
                                   │YES
                                   ▼
                   ┌───────────────────────────────┐
                   │ Does FTSOv2 Price Drop >=     │───NO────► NO_ACTION (Below Trigger)
                   │ Private Trigger Threshold?    │
                   └───────────────┬───────────────┘
                                   │YES
                                   ▼
                             APPROVED (APPROVED)
                    Cap Amount = min(balance * ratio, maxNotional)
```

---

## 🛡️ 2. Non-LLM Security Guarantee

1. **Zero LLM Authorization**: OpenAI models are 100% excluded from financial decision loops.
2. **Deterministic Outputs**: Given identical inputs $(P_{\text{ref}}, P_{\text{current}}, \text{policy})$, the TEE engine produces identical binary decisions (`APPROVED` or `NO_ACTION`).
3. **Auditable Rules**: All evaluation criteria are written in open, verifiable code (`evaluator.js`).
