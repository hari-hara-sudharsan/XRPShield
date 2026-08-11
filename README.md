
# 🛡️ XRPShield

### Confidential Treasury Infrastructure for XRP

<p align="center">
  <strong>Protect the strategy. Verify the outcome.</strong>
</p>

<p align="center">
  Privacy-preserving treasury management powered by Flare Confidential Compute.
</p>

---

## 🚀 What is XRPShield?

**XRPShield** is a privacy-preserving treasury management platform for businesses, merchants, DAOs, and crypto treasuries holding XRP-linked assets.

Traditional blockchain applications force a difficult choice:

- **Public blockchain** → transparent and verifiable, but sensitive financial strategies can be exposed.
- **Traditional finance** → private, but dependent on centralized intermediaries.
- **AI automation** → convenient, but financial decisions may depend on opaque models.

XRPShield takes a different approach.

It allows treasury users to define sensitive financial policies, process those policies through **Flare Confidential Compute**, and produce verifiable decisions without requiring the complete private strategy to become public blockchain state.

### The core idea

```text
                        ┌──────────────────────┐
                        │   Private Strategy   │
                        │                      │
                        │  Risk Parameters     │
                        │  Protection Rules    │
                        │  Treasury Conditions │
                        └──────────┬───────────┘
                                   │
                                   ▼
                        ┌──────────────────────┐
                        │ Flare Confidential   │
                        │ Compute              │
                        │                      │
                        │ Private Evaluation   │
                        └──────────┬───────────┘
                                   │
                                   ▼
                        ┌──────────────────────┐
                        │  Attested Decision   │
                        └──────────┬───────────┘
                                   │
                                   ▼
                        ┌──────────────────────┐
                        │ Verifiable Blockchain│
                        │ State & Audit Trail  │
                        └──────────────────────┘
```

> **XRPShield keeps the strategy confidential while keeping the outcome verifiable.**

---

## ✨ Why XRPShield?

Digital-asset treasury management often requires sensitive decisions about:

- Treasury exposure
- Risk thresholds
- Protection ratios
- Leverage limits
- Execution conditions
- Policy expiry
- Asset allocation

On a transparent blockchain, publishing these rules can expose commercially sensitive information.

For example:

```text
Protect 70% of our XRP exposure
when a predefined downside condition is reached.

Never exceed our leverage limit.

Stop the policy after its expiry.
```

The strategy itself may be valuable information.

XRPShield therefore separates **what the treasury wants to do** from **what the blockchain needs to verify**.

---

# 🎯 The Problem

## Public blockchains are transparent by design

Blockchain transparency is valuable for verification, but it can become a problem when financial strategy itself is sensitive.

A treasury may not want competitors, counterparties, or other market participants to see:

- How much exposure it has
- When it intends to protect that exposure
- What risk thresholds it uses
- How much protection it applies
- What conditions trigger execution
- When a strategy expires

Publishing all of this can reveal internal financial policy.

---

## Traditional finance solves privacy differently

Traditional financial institutions can keep financial strategies private, but this generally requires trusting centralized systems and intermediaries.

XRPShield aims to preserve the privacy benefits of confidential computation while retaining blockchain-based verifiability.

---

## AI introduces another trust problem

AI can make financial applications easier to use, but an AI model should not automatically become the authority controlling treasury funds.

XRPShield therefore treats AI as an **assistance layer**, not the financial execution authority.

---

# 💡 The XRPShield Solution

XRPShield divides treasury management into separate trust boundaries:

```text
                              Policy Creation
                                    │
                                    ▼
                              Confidential Evaluation
                                    │
                                    ▼
                              Decision Attestation
                                    │
                                    ▼
                              Decision Verification
                                    │
                                    ▼
                              Execution Authorization
                                    │
                                    ▼
                              Blockchain Settlement
                                    │
                                    ▼
                              Audit Trail
```

This architecture allows the sensitive policy to remain inside the confidential computation workflow while the resulting decision can be independently verified.

---

# 🔐 Confidential Treasury Policies

A treasury policy can contain sensitive parameters such as:

```text
Protection Ratio
Maximum Exposure
Maximum Leverage
Maximum Drawdown
Trigger Condition
Expiry
```

Instead of publishing every parameter as public blockchain state, XRPShield can process sensitive policy information through the supported **Flare Confidential Compute** workflow.

Conceptually:

```text
                                  Private Policy
                                       │
                                       ▼
                                  Private Inputs
                                       │
                                       ▼
                                  Confidential Evaluation
                                       │
                                       ▼
                                  Attested Result
                                       │
                                       ▼
                                  Verifiable Decision
```

---

# 🌐 Why Flare?

Flare is a fundamental part of XRPShield's architecture.

The platform requires both programmable blockchain infrastructure and confidential computation for its treasury workflow.

## Flare Smart Contracts

The on-chain layer provides infrastructure for:

- Treasury vault state
- Ownership
- Policy commitments
- Decision references
- Execution state
- Settlement-related records

## Flare Confidential Compute

The confidential-compute layer is responsible for sensitive operations such as:

- Confidential policy evaluation
- Private computation
- Attested results

## XRP / FXRP Ecosystem

XRPShield is designed around XRP-linked assets and their programmable financial workflows on Flare, where supported by the deployed environment.

Therefore, Flare is not simply being used as a blockchain deployment target.

> **Confidential Compute is part of XRPShield's core trust model.**

---

# 🏗️ Architecture

At a high level, XRPShield consists of several cooperating layers:

```text
                                             ┌───────────────┐
                                             │     User      │
                                             └───────┬───────┘
                                                     │
                                                     ▼
                                             ┌───────────────┐
                                             │  XRPShield UI │
                                             └───────┬───────┘
                                                     │
                                    ┌────────────────┼────────────────┐
                                    │                │                │
                                    ▼                ▼                ▼
                            ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
                            │   Treasury   │ │    Policy    │ │      AI      │
                            │    Vault     │ │    Engine    │ │  Assistant   │
                            └──────┬───────┘ └──────┬───────┘ └──────┬───────┘
                                   │                │                │
                                   └────────────────┼────────────────┘
                                                    │
                                                    ▼
                                        ┌────────────────────────┐
                                        │ Confidential Policy    │
                                        │ Workflow               │
                                        └────────────┬───────────┘
                                                     │
                                                     ▼
                                        ┌────────────────────────┐
                                        │ Flare Confidential     │
                                        │ Compute                │
                                        └────────────┬───────────┘
                                                     │
                                                     ▼
                                             ┌───────────────┐
                                             │  Attestation  │
                                             └───────┬───────┘
                                                     │
                                                     ▼
                                        ┌────────────────────────┐
                                        │ Decision Validation    │
                                        └────────────┬───────────┘
                                                     │
                                                     ▼
                                        ┌────────────────────────┐
                                        │ Execution Authorization│
                                        └────────────┬───────────┘
                                                     │
                                                     ▼
                                        ┌────────────────────────┐
                                        │ Flare Smart Contracts  │
                                        └────────────┬───────────┘
                                                     │
                                                     ▼
                                        ┌────────────────────────┐
                                        │ Settlement / Blockchain│
                                        │ State                  │
                                        └────────────────────────┘
```

---

# 🔄 How XRPShield Works

## 1. Connect a Wallet

The user connects an EVM-compatible wallet to XRPShield.

The wallet provides the user's blockchain identity and authorization context.

---

## 2. Create a Treasury Vault

The user creates a treasury vault.

The vault maintains the required ownership and asset state on-chain.

```text
Wallet
   │
   ▼
Treasury Vault
   │
   ▼
On-chain Ownership & State
```

---

## 3. Fund the Vault

The user deposits a supported XRP-linked asset into the vault.

The application tracks the blockchain transaction and synchronizes the resulting treasury state.

---

## 4. Define a Treasury Policy

The user defines how the treasury should be managed.

Example:

```text
Protection Ratio:   70%
Maximum Exposure:   User Defined
Maximum Leverage:   2x
Maximum Drawdown:   User Defined
Trigger:            User Defined
Expiry:             User Defined
```

The policy can then be versioned and committed.

---

# 🤖 AI-Assisted Policy Creation

XRPShield can optionally use OpenAI to help users create treasury policies using natural language.

For example:

> Protect most of my XRP treasury while limiting leverage to 2x.

The AI assistant can transform that request into a structured policy proposal.

```text
Natural Language
       │
       ▼
     OpenAI
       │
       ▼
Structured Policy
       │
       ▼
User Review
       │
       ▼
User Confirmation
       │
       ▼
Confidential Policy Workflow
```

### AI Security Boundary

AI is deliberately separated from financial execution.

OpenAI:

- Helps interpret user intent
- Generates policy proposals
- Helps explain policy parameters
- Can assist with reporting

OpenAI does **not** independently authorize treasury transactions.

The user remains responsible for reviewing and confirming the policy before it enters the confidential financial workflow.

---

# 🔒 Confidential Evaluation

After the user confirms a policy, the sensitive policy information enters the supported Flare Confidential Compute workflow.

The system evaluates the policy using authorized inputs.

Conceptually:

```text
                      ┌────────────────────────┐
                      │    Confidential Data   │
                      │                        │
                      │  Policy Parameters     │
                      │  Risk Conditions       │
                      │  Authorized Inputs     │
                      └───────────┬────────────┘
                                  │
                                  ▼
                      ┌────────────────────────┐
                      │ Flare Confidential     │
                      │ Compute                │
                      └───────────┬────────────┘
                                  │
                                  ▼
                      ┌────────────────────────┐
                      │    Private Evaluation  │
                      └───────────┬────────────┘
                                  │
                                  ▼
                      ┌────────────────────────┐
                      │    Attested Result     │
                      └────────────────────────┘
```

The resulting decision can then be passed into the verification workflow.

---

# ✅ Decision Verification

A policy evaluation can produce a decision such as:

```text
APPROVED
```

or:

```text
REJECTED
```

The corresponding attestation can be validated by the application and, where implemented, referenced by on-chain state.

Example application state:

```text
┌──────────────────────────────┐
│      POLICY EVALUATED        │
├──────────────────────────────┤
│ Decision:      APPROVED      │
│ Attestation:   VERIFIED      │
└──────────────────────────────┘
```

The goal is to make the important result verifiable without publishing the entire sensitive strategy.

---

# ⚙️ Execution Pipeline

Approved decisions enter a controlled execution workflow.

```text
                 Decision
                    │
                    ▼
               Validation
                    │
                    ▼
              Authorization
                    │
                    ▼
             Execution Queue
                    │
                    ▼
            Execution Adapter
                    │
                    ▼
         Supported Settlement
                    │
                    ▼
          Blockchain Transaction
                    │
                    ▼
               Confirmation
                    │
                    ▼
               Audit Record
```

Execution is intentionally separated from policy evaluation.

This separation provides an important security boundary:

> **A policy evaluation should not automatically become unrestricted control over treasury funds.**

The execution layer only acts on eligible and validated decisions.

---

# 🛡️ Security Architecture

XRPShield uses multiple security boundaries throughout the treasury lifecycle.

```text
Wallet Authentication
        │
        ▼
Authorization
        │
        ▼
Vault Ownership
        │
        ▼
Policy Integrity
        │
        ▼
Confidential Evaluation
        │
        ▼
Attestation
        │
        ▼
Decision Validation
        │
        ▼
Execution Authorization
        │
        ▼
Blockchain Settlement
        │
        ▼
Audit Trail
```

The system is designed to fail closed when required authorization, attestation, or blockchain conditions cannot be validated.

---

# 🔏 Privacy Model

XRPShield is **not** intended to provide complete transaction anonymity.

Instead, it focuses on protecting sensitive financial strategy and computation within the supported confidential-compute architecture.

## Public / Verifiable

Depending on the deployed implementation, information that can remain verifiable may include:

- Vault address
- Ownership/state
- Transaction hashes
- Decision status
- Timestamps
- Policy commitments
- Attestation references
- Settlement references
- Execution references

## Confidential

The confidential-compute workflow can protect information such as:

- Sensitive policy parameters
- Private evaluation inputs
- Confidential decision logic
- Sensitive execution information

The intended boundary is:

```text
             PRIVATE
                │
                ▼
        Treasury Strategy
                │
                ▼
      Confidential Compute
                │
                ▼
        Attested Decision
                │
                ▼
              PUBLIC
                │
                ▼
       Verifiable State
                │
                ▼
          Audit Evidence
```

---

# 🧩 Core Modules

## Treasury Dashboard

Centralized interface for viewing:

- Vaults
- Assets
- Policies
- Decisions
- Executions
- System status

---

## Vault Management

Provides functionality for:

- Creating vaults
- Funding vaults
- Viewing balances
- Tracking transactions
- Managing vault state

---

# Smart Contract Architecture

XRPShield uses a modular smart-contract architecture to separate
treasury ownership, policy management, decision verification, and
execution authorization.

```text
                    XRPShield Contracts
                           │
            ┌──────────────┼──────────────┐
            │              │              │
            ▼              ▼              ▼
      TreasuryVault   PolicyManager   DecisionRegistry
            │              │              │
            └──────────────┼──────────────┘
                           │
                           ▼
                  ExecutionManager
                           │
                           ▼
                   Settlement Layer
```
## Audit System

Tracks important events across the treasury lifecycle:

```text
Wallet
  │
  ▼
Vault
  │
  ▼
Policy
  │
  ▼
Confidential Compute
  │
  ▼
Decision
  │
  ▼
Execution
  │
  ▼
Blockchain
```

---

# 🛠️ Technology Stack

| Component | Technology |
|---|---|
| Frontend | HTML5, CSS3, JavaScript |
| Backend | Java, Spring Boot |
| API | REST |
| Database | Supabase / PostgreSQL |
| Blockchain | Flare |
| Smart Contracts | Solidity |
| Development Framework | Hardhat |
| Confidential Computing | Flare Confidential Compute |
| AI | OpenAI API |
| Wallet | EVM-compatible wallet |
| Infrastructure | GitHub, HTTPS, Hosting, Monitoring |

---

# 📦 Installation

> **Note:** The exact installation commands should follow the final repository structure. The source material currently does not specify the repository URL or exact build scripts.

### Clone the repository

```bash
git clone <repository-url>
cd xrp-shield
```

### Install frontend dependencies

```bash
npm install
```

### Configure the backend

Configure the required Spring Boot environment and database connection.

### Configure blockchain access

Provide the appropriate Flare RPC configuration and wallet/network settings.

### Configure AI access

If AI-assisted policy creation is enabled, configure the OpenAI API credentials through environment variables.

> **Never commit API keys, private keys, seed phrases, or other credentials to Git.**

---

# ⚙️ Configuration

Configuration should be provided through environment variables or the project's existing configuration system.

Typical configuration categories include:

```env
OPENAI_API_KEY=<your-api-key>

DATABASE_URL=<your-database-url>

FLARE_RPC_URL=<your-flare-rpc-url>

CHAIN_ID=<configured-chain-id>
```


## TreasuryVault

### Purpose

`TreasuryVault` manages the treasury's on-chain ownership and asset
state.

### Responsibilities

- Create treasury vaults
- Track vault ownership
- Accept supported assets
- Track balances
- Restrict unauthorized operations
- Emit treasury lifecycle events

### State

| Variable | Type | Description |
|---|---|---|
| `owner` | `address` | Vault owner |
| `asset` | `address` | Supported treasury asset |
| `balance` | `uint256` | Current treasury balance |

### Functions

#### `createVault()`

Creates a new treasury vault.

#### `deposit()`

Deposits a supported asset into the vault.

#### `withdraw()`

Withdraws assets subject to authorization rules.

### Events

- `VaultCreated`
- `Deposit`
- `Withdrawal`

### Security

- Owner authorization
- Asset validation
- Reentrancy protection
- State validation

# 📋 Example Treasury Policy

A user might create a policy such as:

```text
Protection Ratio: 70%
Maximum Leverage: 2x
Maximum Exposure: User Defined
Maximum Drawdown: User Defined
Trigger Condition: User Defined
Expiry: User Defined
```

The resulting workflow is:

```text
Policy Created
      │
      ▼
User Confirmation
      │
      ▼
Confidential Evaluation
      │
      ▼
Attestation
      │
      ▼
Decision Verification
      │
      ▼
Execution Authorization
      │
      ▼
Settlement
      │
      ▼
Audit Trail
```

---

# 🎯 Use Cases

### Businesses

Manage XRP treasury exposure while keeping internal risk policies confidential.

### Merchants

Manage treasury exposure resulting from XRP payments.

### DAOs

Apply programmable treasury policies without exposing every internal financial rule.

### Crypto Treasuries

Create structured and verifiable treasury-management workflows.

### Payment Businesses

Support privacy-sensitive XRP and digital-asset settlement workflows.

---




# 📄 License

License information will be added according to the project's final repository configuration.

---

# ⚠️ Disclaimer

XRPShield is software infrastructure for programmable digital-asset treasury management.

Nothing in this project constitutes financial, investment, legal, or tax advice.

Digital assets, smart contracts, blockchain transactions, confidential-compute systems, and external execution infrastructure involve technical and financial risks.

Users are responsible for evaluating those risks before using the system.

---

<div align="center">

## 🛡️ XRPShield

### Protect the strategy. Verify the outcome.

**Confidential Treasury Infrastructure for XRP**

</div>

