# XRPShield

## The Confidential Treasury Infrastructure of Flare

**Private Strategy. Verifiable Decision. On-Chain Protection.**

> Protect the strategy. Verify the decision. Execute on-chain.

XRPShield is confidential treasury-policy infrastructure that allows XRP
holders to define private protection policies, evaluate those policies
through Flare Confidential Compute, cryptographically verify the
resulting decision, and execute bounded on-chain protection without
exposing the treasury's sensitive strategy.

------------------------------------------------------------------------

##  What XRPShield Does

XRPShield separates **private treasury strategy** from **publicly
verifiable execution**.

The core lifecycle is:

``` text
PRIVATE POLICY
      ↓
POLICY COMMITMENT
      ↓
CONFIDENTIAL COMPUTE
      ↓
AUTHENTICATED ACTIONRESULT
      ↓
SMART-CONTRACT VERIFICATION
      ↓
RISK-BOUNDED EXECUTION
      ↓
FXRP → USDT0
      ↓
SETTLEMENT PROOF
```

The strategy remains confidential while the resulting financial action
remains independently verifiable.

------------------------------------------------------------------------

##  The Problem

XRP-linked treasury value can change rapidly. A treasury may want
predefined protection rules such as:

-   protect a defined portion of XRP exposure after a downside
    threshold;
-   never exceed an approved protection limit;
-   never execute after a policy deadline;
-   never execute against stale market data.

Publishing those rules directly on a transparent blockchain can expose:

-   treasury thresholds;
-   hedge ratios;
-   maximum protection limits;
-   execution timing;
-   risk strategy;
-   sensitive policy inputs and evaluation logic.

This creates a fundamental trade-off:

``` text
Privacy
   vs
Verifiability
```

XRPShield is designed to remove that trade-off.

------------------------------------------------------------------------

##  Why XRPShield Is Different

A conventional dashboard is:

``` text
Price
 ↓
Button
 ↓
Swap
```

XRPShield is:

``` text
PRIVATE TREASURY INTENT
        ↓
POLICY BINDING
        ↓
CONFIDENTIAL EVALUATION
        ↓
CRYPTOGRAPHIC DECISION
        ↓
ON-CHAIN AUTHORIZATION
        ↓
BOUNDED EXECUTION
        ↓
SETTLEMENT PROOF
```


##  The XRPShield Solution

XRPShield separates the system into two trust domains.

### Private

``` text
Treasury Strategy
      ↓
Policy Commitment
      ↓
Confidential Evaluation
      ↓
ActionResult
```

### Verifiable

``` text
Cryptographic Verification
      ↓
Smart Contract Authorization
      ↓
DEX Execution
      ↓
FXRP → USDT0
      ↓
Transaction Receipt
      ↓
Independent Verification
```

### Core boundary

**The strategy remains confidential. The financial action remains
independently verifiable.**

------------------------------------------------------------------------

##  Why Flare Is Fundamental

XRPShield depends on Flare for multiple parts of its trust and execution
model.

  Flare capability             XRPShield role
  ---------------------------- ------------------------------------
  FXRP                         XRP-linked treasury asset
  FTSOv2                       XRP/USD market-data input
  Flare Confidential Compute   Private policy evaluation
  Smart contracts              Authorization and enforcement
  Coston2                      Demonstrated execution environment
  DEX infrastructure           FXRP → USDT0 settlement

``` text
                    FLARE
                      │
          ┌───────────┼───────────┐
          ▼           ▼           ▼
        FXRP        FTSOv2        FCC
          │        XRP/USD        │
          │           │           │
          └───────────┼───────────┘
                      ▼
                Smart Contracts
                      │
                      ▼
                  DEX Execution
                      │
                      ▼
                    Coston2
```

------------------------------------------------------------------------

##  Trust and Authority Model

The financial authority is deliberately constrained.

> **AI does not authorize. Backend does not authorize. Database does not
> authorize. Frontend does not authorize.**

Authorization exists only after the required cryptographic and
smart-contract checks succeed.

``` text
User
 ↓
Policy
 ↓
Commitment
 ↓
Confidential Evaluation
 ↓
Authenticated Result
 ↓
Smart Contract
 ↓
Execution Controls
 ↓
Settlement
```

### Evidence hierarchy

``` text
FLARE BLOCKCHAIN
       ↓
SMART CONTRACT STATE
       ↓
VERIFIED ACTIONRESULT
       ↓
TRANSACTION RECEIPT
       ↓
BACKEND INDEX
       ↓
FRONTEND
```

Lower-level presentation or indexing data cannot override higher-level
blockchain evidence.

------------------------------------------------------------------------

##  System Architecture

``` text
                      TREASURY USER
                           │
                           ▼
                      XRPSHIELD APP
                           │
             ┌─────────────┼─────────────┐
             ▼             ▼             ▼
          WALLET        POLICY         PROOF
                       BUILDER         CENTER
             │             │             │
             └─────────────┼─────────────┘
                           ▼
                     APPLICATION API
                           │
             ┌─────────────┼─────────────┐
             ▼             ▼             ▼
        SPRING BOOT       FCC        BLOCKCHAIN
             │          ADAPTER           │
             ▼             │              ▼
         SUPABASE       FCC FLOW       COSTON2
                           │
                           ▼
                       ACTIONRESULT
                           │
                           ▼
                    SMART CONTRACTS
                           │
                           ▼
                      DEX EXECUTION
```
## End - End Architecture

``` text
       Treasury Owner
     │
     ▼
XRPShield Application
     │
     ├── Treasury Management
     ├── Private Policy Management
     ├── Risk Dashboard
     ├── Decision Center
     └── Verification Center
     │
     ▼
Policy Encryption + Commitment
     │
     ├── Encrypted Policy ───────────────► Confidential Runtime
     │
     └── Policy Commitment ──────────────► XRPShieldVault
                                             │
                                             ▼
                                      FCC Instruction
                                             │
                                             ▼
                                  Confidential Evaluation
                                             │
                         ┌───────────────────┴───────────────────┐
                         │                                       │
                         ▼                                       ▼
                  Private Policy                           FTSOv2 Price
                         │                                       │
                         └───────────────────┬───────────────────┘
                                             ▼
                                      Risk Decision
                                             │
                                      APPROVED / NO_ACTION
                                             │
                                             ▼
                                      ActionResult
                                             │
                                             ▼
                                     EIP-712 Signature
                                             │
                                             ▼
                                  FCCExtensionAdapter
                                             │
                       ┌─────────────────────┼─────────────────────┐
                       │                     │                     │
                       ▼                     ▼                     ▼
                  Signer Check          Policy Check          Replay Check
                       │                     │                     │
                       └─────────────────────┼─────────────────────┘
                                             ▼
                                    XRPShieldVault
                                             │
                                             ▼
                                     HedgeExecutor
                                             │
                          ┌──────────────────┼──────────────────┐
                          │                  │                  │
                          ▼                  ▼                  ▼
                     Asset Check       Amount Check       Slippage Check
                          │                  │                  │
                          └──────────────────┼──────────────────┘
                                             ▼
                                      DEX Router
                                             │
                                             ▼
                                       FXRP → USDT0
                                             │
                                             ▼
                                      Treasury Vault
                                             │
                                             ▼
                                  Settlement Receipt
                                             │
                                             ▼
                                  Independent Verifier
```

### Application responsibilities

Spring Boot provides application orchestration and indexing, including:

-   REST API;
-   authentication;
-   wallet ownership verification;
-   policy metadata;
-   OpenAI integration;
-   FCC orchestration;
-   blockchain interaction;
-   event indexing;
-   transaction monitoring;
-   analytics;
-   audit history;
-   proof APIs.

Supabase stores application and indexed blockchain information. It is
**not** the financial source of truth.

------------------------------------------------------------------------

##  Policy Architecture

A treasury policy contains the conditions under which protection may
occur.

``` text
Policy
├── Asset
├── Trigger condition
├── Protection ratio
├── Maximum hedge
├── Execution constraints
├── Deadline
├── Nonce
└── Version
```

### Example policy

``` text
Asset:               FXRP
Hedge Ratio:         70%
Trigger:             XRP downside threshold
Maximum Protection:  500 FXRP
Deadline:            Specified expiration
Nonce:               Unique execution identifier
Version:             Policy version
```

### Policy commitment

The policy is deterministically encoded and converted into a
cryptographic commitment:

``` text
Policy
   ↓
Canonical Encoding
   ↓
keccak256
   ↓
Policy Commitment
```

Conceptually:

``` text
policyCommitment =
keccak256(
    vaultId,
    asset,
    hedgeRatio,
    triggerThreshold,
    maximumProtection,
    deadline,
    nonce,
    policyVersion
)
```

The commitment binds:

``` text
USER-APPROVED POLICY
        ↕
CONFIDENTIAL EVALUATION
        ↕
ON-CHAIN EXECUTION
```

A result generated for another policy must be rejected.

### Policy versioning

``` text
Policy V1 → Commitment V1
Policy V2 → Commitment V2
Policy V3 → Commitment V3
```

An ActionResult associated with one policy version cannot authorize
execution under another.

------------------------------------------------------------------------

##  OpenAI Policy Assistant

OpenAI provides a human-friendly policy interface.

For example:

> "Protect 70% of my XRP exposure if XRP falls by 5%, never exceed 500
> FXRP, and do not execute after tomorrow."

The assistant can structure the request as a policy proposal:

``` json
{
  "hedgeRatio": 70,
  "triggerThreshold": 5,
  "maximumProtection": 500,
  "deadline": "...",
  "policyVersion": 1
}
```

The user reviews the proposal before it becomes an approved policy.

### AI authority boundary

``` text
OpenAI
  │
  ├── Recommend
  ├── Structure
  ├── Explain
  └── Assist
       │
       X
       ├── Sign
       ├── Execute
       ├── Move funds
       ├── Approve hedge
       └── Override contract
```

**AI recommends. Cryptography authenticates. Smart contracts enforce.**

------------------------------------------------------------------------

##  Flare Confidential Compute

FCC is the confidentiality layer and central privacy component.

``` text
On-chain Instruction
        │
        ▼
InstructionSender
        │
        ▼
Flare Confidential Compute
        │
        ▼
Confidential Extension
        │
        ▼
Private Evaluation
        │
        ▼
ActionResult
        │
        ▼
Authentication
        │
        ▼
Smart Contract Verification
        │
        ▼
Execution Authorization
```

The confidential evaluation uses sensitive policy information together
with market-data context, risk rules and execution constraints.

------------------------------------------------------------------------

##  ActionResult and EIP-712 Verification

The confidential evaluation produces an authenticated `ActionResult`.

Conceptual fields include:

``` text
ActionResult
├── vaultId
├── policyCommitment
├── decision
├── hedgeAmount
├── nonce
├── timestamp
└── deadline
```

The contract validates the expected vault, policy, nonce, deadline,
signer and execution context.

### Cryptographic path

``` text
ActionResult
     ↓
Typed Data
     ↓
EIP-712 Domain
     ↓
Digest
     ↓
Signature
     ↓
Signer Recovery
     ↓
Registered Identity
     ↓
VALID / REJECT
```

The verification boundary protects against:

-   forged results;
-   modified results;
-   wrong policy;
-   wrong vault;
-   unauthorized signer;
-   replay;
-   expired authorization.

------------------------------------------------------------------------

##  Smart Contract Architecture

XRPShield uses a responsibility-separated financial control plane.

  -----------------------------------------------------------------------
  Contract                            Financial responsibility
  ----------------------------------- -----------------------------------
  `XRPShieldVault`                    Treasury custody, policy binding
                                      and authorization

  `FCCExtensionAdapter`               Confidential result → authenticated
                                      on-chain decision

  `HedgeExecutor`                     Bounded FXRP → USDT0 execution
  
  -----------------------------------------------------------------------

### Contract relationship

``` text
                 ┌─────────────────────┐
                 │ XRPShieldVault.sol  │
                 │                     │
                 │ Custody             │
                 │ Policy              │
                 │ Verification        │
                 │ Authorization       │
                 │ Replay Protection   │
                 │ Emergency Controls  │
                 └──────────┬──────────┘
                            │
              ┌─────────────┴─────────────┐
              ▼                           ▼
      ┌──────────────────┐       ┌──────────────────┐
      │ FCC Extension    │       │ HedgeExecutor    │
      │ Adapter          │      │                  │
      │                  │       │ Router Control   │
      │ Result Validation│       │ Slippage         │
      │ Signature        │       │ Deadline         │
      │ Policy Binding   │       │ Output Bounds    │
      └──────────────────┘       └────────┬─────────┘
                                         │
                                         ▼
                                      DEX Router
                                         │
                                         ▼
                                      FXRP → USDT0
```

### XRPShieldVault

The vault is the final on-chain authorization boundary.

Responsibilities include:

-   vault creation;
-   ownership;
-   FXRP deposit and withdrawal;
-   policy commitment;
-   policy versioning;
-   FCC instruction binding;
-   ActionResult verification;
-   nonce consumption;
-   execution authorization;
-   emergency pause.

The vault must never trust frontend state, backend state, database state
or AI output.

### FCCExtensionAdapter

``` text
FCC Result
    ↓
ActionResult
    ↓
EIP-712 Signature
    ↓
Signer Recovery
    ↓
Policy Binding
    ↓
Nonce Check
    ↓
Expiry Check
    ↓
Execution Authorization
```

### HedgeExecutor

The HedgeExecutor performs the bounded asset conversion. It does not
independently decide whether a hedge should occur.

``` text
FXRP
 ↓
Approved Router
 ↓
Approved Route
 ↓
USDT0
```

------------------------------------------------------------------------

##  Vault State Machine

``` text
CREATED
   ↓
FUNDED
   ↓
POLICY_ACTIVE
   ↓
EVALUATION_REQUESTED
   ↓
DECISION_RECEIVED
   ↓
DECISION_VERIFIED
   ↓
EXECUTION_AUTHORIZED
   ↓
EXECUTED
```

Failure states include:

``` text
EVALUATION_REQUESTED → FAILED
DECISION_RECEIVED    → INVALID_ATTESTATION
EXECUTION_AUTHORIZED → SWAP_FAILED
```

A failed operation must never become a successful UI state.

------------------------------------------------------------------------

##  Market Data and Asset Integrations

### FTSOv2

FTSOv2 supplies the XRP/USD market-data input.

``` text
FTSOv2
   ↓
XRP/USD
   ↓
Feed Validation
   ↓
Timestamp Validation
   ↓
Freshness Validation
   ↓
Risk Evaluation
```

Validation includes:

-   supported feed;
-   available value;
-   timestamp;
-   freshness;
-   stale-data threshold.

If price data is stale:

``` text
PRICE DATA STALE
       ↓
EXECUTION BLOCKED
```

FTSOv2 is an input to the risk pipeline, not a cosmetic price display.

### FXRP

FXRP is the XRP-linked asset held by the vault.

``` text
User Wallet
     ↓
FXRP
     ↓
XRPShield Vault
     ↓
Private Policy
     ↓
Authorized Hedge
```

Application balances must remain reconcilable against blockchain state.

### USDT0

USDT0 is the settlement/output asset used by the current MVP hedge path.

------------------------------------------------------------------------

##  Bounded Hedge Execution

The current MVP hedge is an **FXRP → USDT0 conversion** through the
configured Coston2 DEX route.

It should not be described as a perpetual short.

``` text
Approved Decision
       ↓
FTSO Price
       ↓
DEX Quote
       ↓
Slippage Check
       ↓
Deadline Check
       ↓
HedgeExecutor
       ↓
FXRP → USDT0
       ↓
Coston2 Receipt
       ↓
Token Transfer Verification
```

### DEX quote controls

A quote contains:

``` text
tokenIn
tokenOut
amountIn
expectedOutput
minimumOutput
router
deadline
```

Example from the supplied documentation:

``` text
Expected Output:  100 USDT0
Maximum Slippage: 0.5%
Minimum Output:   99.5 USDT0
```

If actual output is below the minimum, execution is rejected.

### Deadline protection

``` text
Authorization
      ↓
Quote
      ↓
Deadline
      ↓
Execution
```

After expiration, execution is blocked.

------------------------------------------------------------------------

##  Coston2 Execution Evidence

The supplied documentation reports a real Coston2 execution:

``` text
10.00 FXRP → 8.4575 USDT0
Block: 33973480

Transaction:
0x3fe85c1668067f91274cab7b46800bd59fe11375eacb1abfe9b5a4e778447cb3
```

The supplied documentation also reports a quoted **0.5% maximum slippage
cap**.

Before final submission, the transaction, receipt and associated
token-transfer evidence should be re-queried against Coston2.

------------------------------------------------------------------------

##  Complete Decision Flow

``` text
1.  Connect Wallet
2.  Create / Select Vault
3.  Deposit FXRP
4.  Define Policy
5.  Commit Policy
6.  Request Confidential Evaluation
7.  Evaluate Sensitive Policy
8.  Produce ActionResult
9.  Verify Cryptographic Authorization
10. Validate FTSOv2 Price and Freshness
11. Obtain DEX Quote
12. Validate Slippage
13. Validate Deadline
14. Authorize Execution
15. Execute FXRP → USDT0
16. Verify Transaction Receipt
17. Verify Token Settlement
```

------------------------------------------------------------------------

##  Security Architecture

``` text
USER INPUT
   ↓
POLICY VALIDATION
   ↓
POLICY COMMITMENT
   ↓
CONFIDENTIAL COMPUTE
   ↓
CRYPTOGRAPHIC VERIFICATION
   ↓
SMART CONTRACT RULES
   ↓
EXECUTION SAFETY
   ↓
ON-CHAIN SETTLEMENT
```

### Security invariants

  Condition                          Failure behavior
  ---------------------------------- ------------------------------
  No verified decision               No execution
  Invalid signature                  Revert
  Wrong policy commitment            Revert
  Wrong vault                        Revert
  Expired result                     Revert / blocked
  Replayed nonce                     Revert
  Excessive hedge amount             Revert
  Stale FTSO data                    Execution blocked
  Excessive slippage                 Revert
  Unauthorized router                Revert
  Arbitrary recipient                Revert
  Paused vault                       Execution blocked
  RPC / infrastructure uncertainty   Never converted into success

### Threat model

The documented threat model covers:

-   forged signatures;
-   modified ActionResults;
-   replay and expiry;
-   wrong policy or vault;
-   unauthorized routers;
-   arbitrary recipients;
-   invalid tokens;
-   excessive amounts;
-   duplicate execution;
-   insufficient balance;
-   excessive slippage;
-   stale or unavailable oracle data;
-   malformed market data;
-   frontend manipulation;
-   backend manipulation;
-   database manipulation;
-   prompt injection;
-   false execution status;
-   RPC uncertainty.

Security-critical failures should fail closed.

------------------------------------------------------------------------

##  Observability and Transaction Integrity

The system tracks or exposes operational conditions including:

-   RPC latency;
-   transaction failures;
-   FCC request latency;
-   FCC errors;
-   ActionResult verification failures;
-   DEX failures;
-   stale FTSO data;
-   indexing failures;
-   circuit-breaker state;
-   infrastructure failures.

Infrastructure uncertainty must never be converted into financial
success.

### Transaction state

``` text
CREATED
   ↓
SUBMITTED
   ↓
PENDING
   ├──→ CONFIRMED
   ├──→ FAILED
   └──→ UNKNOWN
```

A submitted transaction is not automatically successful. A verified
receipt and expected settlement evidence establish confirmed execution.

------------------------------------------------------------------------

##  Independent Verification

The Proof Center answers a central question:

> Can a technically competent judge independently verify that this
> actually happened?

The verification hierarchy is:

``` text
Blockchain State
      ↓
Smart Contract Event
      ↓
Verified Source
      ↓
FCC / ActionResult Evidence
      ↓
Automated Tests
      ↓
Backend Index
      ↓
Frontend
```

### Verification map

  Component / claim   Verification method
  ------------------- ----------------------------------------
  Smart contracts     Flare explorer / verified source
  Vault state         Contract read
  Policy commitment   Contract event/state
  ActionResult        Signature / cryptographic verification
  FTSOv2              On-chain feed data
  Execution           Transaction receipt
  Settlement          Token transfer / event logs
  Security            Automated test suite
  Backend             API / indexed records
  Frontend            Presentation only

### Financial source of truth

``` text
Flare Blockchain
       +
Smart Contract State
       +
Verified ActionResult
       +
Actual Transaction Receipt
       =
FINANCIAL SOURCE OF TRUTH
```

------------------------------------------------------------------------

##  Current Integration Set

  Integration / component      Role
  ---------------------------- -------------------------------------------
  Flare Coston2                Blockchain execution and settlement
  Flare Confidential Compute   Confidential policy evaluation
  FTSOv2                       XRP/USD market data
  FXRP                         XRP-linked treasury asset
  USDT0                        Hedge settlement asset
  DEX                          FXRP → USDT0 execution
  XRPShieldVault               Treasury authority
  FCCExtensionAdapter          Confidential-result verification boundary
  HedgeExecutor                Controlled swap execution
  EIP-712                      Cryptographic ActionResult authentication
  Spring Boot                  Application orchestration
  Supabase                     Indexed application / blockchain data
  OpenAI                       Policy advisory interface

------------------------------------------------------------------------

##  Current Capability and Status Boundaries

The supplied project documentation defines the following boundaries:

  -----------------------------------------------------------------------
  Capability                          Status
  ----------------------------------- -----------------------------------
  Coston2 blockchain execution        Real / implemented testnet
                                      execution

  Smart contracts                     Implemented

  FXRP integration                    Integrated

  FTSOv2 integration                  Integrated

  DEX execution path                  Implemented

  FXRP → USDT0 settlement             Real demonstrated execution

  Backend / indexing                  Implemented

  OpenAI policy assistant             Advisory

  FCC workflow                        Implemented

  Current TEE mode                    Simulated on Coston2

  Production confidential hardware    Not claimed

  Mainnet financial infrastructure    Not claimed

  Perpetual short                     Not claimed

  Absolute anonymity                  Not claimed

  External venue execution            Not claimed unless genuinely
                                      deployed and tested
  -----------------------------------------------------------------------

### Important status boundary

The supplied documentation explicitly states that the current Coston2
environment uses a **simulated TEE mode**. This README therefore does
not represent the current environment as production hardware TEE
attestation.

Coston2 is a testnet. Production mainnet use requires additional
security review and infrastructure validation.

------------------------------------------------------------------------

##  Hackathon Positioning

### Primary track

**Confidential Compute Apps**

The defining feature is confidential treasury-policy evaluation. Without
Confidential Compute, the privacy property that differentiates XRPShield
is lost.

### Core hackathon work documented by the project

-   Confidential treasury-policy workflow
-   Policy commitment and versioning
-   Coston2 vault contracts
-   FXRP integration
-   FTSOv2 market-data integration
-   FCC extension integration
-   ActionResult verification
-   Real FXRP → USDT0 execution
-   Spring Boot orchestration and indexing
-   Policy-assistant workflow
-   Privacy and execution evidence
-   Security testing
-   Judge-facing verification

------------------------------------------------------------------------



The product combines:

**privacy + programmable policy + cryptographic authorization + risk
controls + real settlement + independent verification.**


### Security Principle

AI can advise. The application can request. The confidential runtime can decide. But only authorized smart-contract logic can move treasury assets.

This should be one of the strongest lines in your presentation.
------------------------------------------------------------------------





