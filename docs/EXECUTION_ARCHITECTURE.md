# XRPShield — Protected Treasury Execution Engine Architecture

## 1. Overview
The **Protected Treasury Execution Engine** ensures that only **APPROVED** confidential policy decisions evaluated inside Flare TEE enclaves can transition to on-chain execution on the **Flare Network**.

---

## 2. Protected Execution Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor User as Treasury Manager
    participant App as Spring Boot Service
    participant Gateway as ExecutionGateway (Web3j / Flare RPC)
    participant Contract as VaultManager.sol
    participant DB as Supabase PostgreSQL

    User->>App: POST /api/v1/execution/start
    App->>App: Validate Decision Eligibility (Status: APPROVED)
    App->>Contract: registerExecution(vaultAddress, decisionHash, executionHash, "EXECUTING")
    App->>Gateway: executeOnChain(vaultAddress, executionHash)
    Gateway-->>App: ExecutionTxResult (TxHash, BlockNumber, GasUsed)
    App->>Contract: recordExecutionResult(vaultAddress, executionHash, resultPayload, success)
    App->>DB: Store TreasuryExecution, ExecutionResult, & ExecutionAudit
    App-->>User: Return On-Chain Execution Receipt
```

---

## 3. Execution State Machine

```
   [PENDING]
      │
      ▼
   [QUEUED] ──────► [CANCELLED]
      │
      ▼
 [VALIDATING]
      │
      ▼
  [EXECUTING]
   ┌──┴────────┐
   ▼           ▼
[COMPLETED] [FAILED] (Retry Policy: max 3 attempts)
```

---

## 4. Recovery & Retry Strategy
1. **Idempotency:** Each execution produces a unique `execution_hash`. Multiple duplicate submissions return the original receipt.
2. **Retry Policy:** `ExecutionScheduler` retries failed queue items up to `max_retries = 3` before marking state as `FAILED`.
3. **Audit Trail:** All state transitions and transaction hashes are logged into `execution_audits`.
