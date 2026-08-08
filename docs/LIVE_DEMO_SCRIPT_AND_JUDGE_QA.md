# XRPShield — Live Demo Script & Judge Q&A Guide

## 1. Demo Timeline & Structure (3 Minutes Total)

| Time | Demo Stage | Action & Key Talking Points |
|---|---|---|
| **0:00 - 0:45** | **1. Connect & Overview** | Show SaaS Treasury Dashboard. Connect MetaMask wallet with EIP-191 personal signature. Highlight active 1,250,000 FXRP reserve and subsystem health matrix. |
| **0:45 - 1:30** | **2. Create Confidential Policy** | Navigate to `Confidential Policies`. Set 10% drawdown threshold. Show AES-256-GCM encryption & Flare TEE attestation logging (`FCC-ATT-FD1A77E2`). Emphasize that policy bounds are invisible on-chain. |
| **1:30 - 2:15** | **3. Decision & Protected Execution** | Go to `Decision Engine` and evaluate policy. Show decision state `PROTECT_POSITION`. Execute on-chain under `Protected Executions`, showing block height 1,489,201 and transaction hash receipt. |
| **2:15 - 3:00** | **4. AI Assistant & Wrap-Up** | Open `AI Intelligence`. Ask AI assistant to explain decision rationale. Show how `PromptBuilder.java` redacts private keys before sending prompt to OpenAI. |

---

## 2. Judge Questions & Answers (Q&A Preparation)

### Q1: Why do we need Flare Confidential Compute (FCC) for treasury risk management?
**Answer:** On public blockchains like Flare or Ethereum, smart contract code and storage are completely visible. If a treasury sets a public drawdown protection trigger at 10%, arbitrageurs and front-runners can exploit that knowledge to liquidate positions. FCC TEE enclaves allow us to evaluate risk triggers off-chain in isolated hardware memory while outputting cryptographically verified attestation proofs on-chain.

### Q2: How does XRPShield ensure private keys or confidential data are never exposed to OpenAI?
**Answer:** We implement a strict Privacy Filter Guard (`PromptBuilder.java`) in our backend service layer. Before any prompt payload is sent to the OpenAI API adapter, the sanitizer strips raw private keys, seed phrases, hexadecimal secret hashes (`0x[a-fA-F0-9]{64}`), and encrypted payload bytes. OpenAI receives only sanitized, plain-language intent summaries.

### Q3: What is the transaction finality time on Flare Network for protected execution?
**Answer:** On Flare Coston2 Testnet, Web3j transaction finality averages 1.2 seconds with gas consumption around 65,000 gas per execution recording call.
