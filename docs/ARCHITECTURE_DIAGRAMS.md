# XRPShield — Production Architecture Diagrams

## 1. Overall System Architecture

```mermaid
graph TD
    User([Treasury Manager / DAO User])
    WebUI[Frontend Vanilla JS ES6 SPA]
    SpringBoot[Spring Boot 3.2.3 / Java 21 Backend]
    SupabaseDB[(Supabase PostgreSQL 15+)]
    Web3RPC[Flare Coston2 Testnet RPC]
    FCCTEE[Flare Confidential Compute TEE Enclave]
    OpenAIAdapter[OpenAI GPT-4o API Adapter]

    User <-->|HTTPS / Web3| WebUI
    WebUI <-->|REST API + JWT| SpringBoot
    SpringBoot <-->|JPA / JDBC| SupabaseDB
    SpringBoot <-->|Web3j RPC| Web3RPC
    SpringBoot <-->|TEE Attestation| FCCTEE
    SpringBoot <-->|Sanitizer Guard| OpenAIAdapter
```

---

## 2. Smart Contract Layout (`contracts/contracts/`)

```mermaid
classDiagram
    class AccessManager {
        +hasRole(bytes32, address)
        +grantRole(bytes32, address)
        +isOperator(address)
        +isPauser(address)
    }

    class TreasuryStorage {
        +setVault(address, VaultInfo)
        +setBalance(address, uint256)
        +setPolicyCommitment(address, bytes32)
        +setDecisionHash(address, bytes32)
        +setExecutionHash(address, bytes32)
    }

    class VaultManager {
        +registerVault(address, string, string)
        +deposit(address)
        +withdraw(address, uint256)
        +registerPolicyCommitment(address, bytes32, string)
        +recordPolicyAttestation(address, bytes32, string, bool)
        +registerDecision(address, bytes32, string, string)
        +registerExecution(address, bytes32, bytes32, string)
        +recordExecutionResult(address, bytes32, string, bool)
    }

    VaultManager --> AccessManager : Check Authorization
    VaultManager --> TreasuryStorage : Write State Storage
```

---

## 3. Flare Confidential Compute (FCC) TEE Evaluation Flow

```mermaid
sequenceDiagram
    autonumber
    actor User as Treasury Manager
    participant App as Spring Boot Service
    participant Encrypter as AES-256-GCM Engine
    participant FCC as Flare TEE Enclave
    participant DB as Supabase PostgreSQL
    participant Contract as VaultManager.sol

    User->>App: Define Confidential Policy Bounds
    App->>Encrypter: Encrypt Policy Payload (AES-256-GCM)
    App->>FCC: Submit Encrypted Payload & Policy Hash
    FCC->>FCC: Evaluate inside Hardware Enclave
    FCC-->>App: TEE Hardware Quote + Attestation ID (FCC-ATT-1234)
    App->>Contract: registerPolicyCommitment(vaultAddress, policyHash, metadataUri)
    App->>Contract: recordPolicyAttestation(vaultAddress, policyHash, attestationId, true)
    App->>DB: Store Policy & Attestation Log
```

---

## 4. Protected Treasury Execution Engine Flow

```mermaid
sequenceDiagram
    autonumber
    actor User as Treasury Manager
    participant App as Spring Boot Service
    participant Gateway as ExecutionGateway (Web3j)
    participant Contract as VaultManager.sol

    User->>App: POST /api/v1/execution/start (Decision ID)
    App->>App: Validate Decision Status (APPROVED)
    App->>Contract: registerExecution(vaultAddress, decisionHash, executionHash, "EXECUTING")
    App->>Gateway: executeOnChain(vaultAddress, executionHash)
    Gateway-->>App: ExecutionTxResult (TxHash, BlockNumber, GasUsed)
    App->>Contract: recordExecutionResult(vaultAddress, executionHash, payload, true)
    App-->>User: Return Verified Receipt (COMPLETED)
```
