# XRPShield — Treasury Intelligence Layer Architecture

## 1. Overview
The **Treasury Intelligence Layer** provides an AI Policy Assistant, plain-language Decision Explanation Engine, Vault Insights, and Executive Report Generator. It uses OpenAI models to simplify policy creation and decision explainability without compromising secret keys or TEE enclave memory.

---

## 2. Privacy Boundary & Prompt Flow

```mermaid
sequenceDiagram
    autonumber
    actor User as Treasury Manager
    participant App as PolicyAssistantService
    participant Sanitizer as PromptBuilder (Privacy Filter)
    participant OpenAI as OpenAI API Client / Adapter
    participant DB as Supabase PostgreSQL

    User->>App: POST /api/v1/ai/policy (Natural Language Intent)
    App->>Sanitizer: buildSanitizedPrompt(rawPrompt)
    Sanitizer-->>App: Sanitized Prompt (Stripped Keys/Hashes)
    App->>OpenAI: Request Completion (Sanitized Context Only)
    OpenAI-->>App: AI Response (Draft Policy JSON / Explanation)
    App->>DB: Store PolicyDraft / AIConversation
    App-->>User: Return Structured AI Output
```

---

## 3. Strict Privacy Boundaries
- **NEVER SENT TO AI:** Private keys, seed phrases, raw FCC TEE enclave memory, encrypted payload bytes, database credentials.
- **APPROVED FOR AI:** Vault names, public decision types (`PROTECT_POSITION`, `REDUCE_EXPOSURE`), public attestation IDs (`FCC-ATT-1234`), public drawdown threshold percentages.
