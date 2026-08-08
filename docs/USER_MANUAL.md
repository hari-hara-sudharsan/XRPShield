# XRPShield — User Manual & Troubleshooting Guide

## 1. Quickstart User Journey
1. **Connect MetaMask Wallet:** Click `🦊 Connect Wallet` on the topbar. Sign the EIP-191 personal signature prompt.
2. **Create FXRP Vault:** Navigate to `🔒 Vault Management` and click `+ New FXRP Vault`.
3. **Commit Confidential Risk Policy:** Go to `🛡️ Confidential Policies` and set your maximum drawdown threshold (e.g. 10.0%). The payload will be encrypted via AES-256-GCM and verified in Flare TEE enclaves.
4. **Evaluate Treasury Decision:** Navigate to `⚡ Decision Engine` to trigger TEE policy evaluation.
5. **Execute Protected Action:** Under `🚀 Protected Executions`, submit approved decisions on-chain to receive a verified transaction receipt.
6. **Use AI Assistant:** Ask the `🤖 AI Intelligence` assistant to draft policies or explain decision rationale.

---

## 2. Frequently Asked Questions (FAQ)
- **Q: Are my confidential policy parameters visible on-chain?**  
  *A: No. Policy parameters are encrypted using AES-256-GCM and evaluated exclusively inside Flare Confidential Compute (FCC) enclaves. Only policy hashes and TEE attestation proof IDs are saved on-chain.*
- **Q: Does XRPShield execute autonomous trades without user approval?**  
  *A: No. Execution requires explicit user initiation for APPROVED decisions.*
