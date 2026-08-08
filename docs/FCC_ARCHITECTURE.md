# Flare Confidential Compute (FCC) Integration Architecture

## 1. Overview
**Flare Confidential Compute (FCC)** allows XRPShield to evaluate confidential treasury policies inside Trusted Execution Environments (TEEs) without revealing sensitive parameters (risk thresholds, stop loss values, drawdown limits) to public nodes or third parties.

---

## 2. Enclave Execution Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor User as Treasury Manager
    participant App as Spring Boot Service
    participant KMS as PolicyEncryptionService (AES-256)
    participant FCC as FCCClient / TEE Enclave
    participant DB as Supabase PostgreSQL
    participant Contract as VaultManager.sol

    User->>App: POST /api/v1/policies
    App->>KMS: encrypt(sensitiveRulesJson)
    KMS-->>App: EncryptedPayload (AES-256-GCM + IV + Tag)
    App->>FCC: submitToEnclave(EncryptedPayload, PolicyHash)
    FCC-->>App: FCCExecutionResult + AttestationQuoteHash
    App->>Contract: registerPolicyCommitment(vaultAddress, policyHash, metadataUri)
    App->>Contract: recordPolicyAttestation(vaultAddress, policyHash, attestationId, status)
    App->>DB: Store ConfidentialPolicy, EncryptedPolicy, & PolicyAttestation
    App-->>User: Return Public Policy Metadata & Verified Attestation Status
```

---

## 3. TEE Attestation Verification Flow
1. **Quote Generation:** The TEE enclave generates an attestation quote hash (`enclave_quote_hash`) signed by the hardware root of trust.
2. **Backend Validation:** `AttestationService` receives the quote and validates the enclave signature.
3. **On-Chain Recording:** `VaultManager.sol` emits `PolicyAttestationRecorded` for public verifiability on Flare Network.
