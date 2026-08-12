# XRPShield Phase 1 — EIP-712 ActionResult Schema & Signature Verification Specification

---

## 📜 1. EIP-712 Typed Data Domain Separator

All `ActionResult` payloads produced by the Flare Confidential Compute (FCC) extension are cryptographically signed using EIP-712 typed structured data.

```solidity
DOMAIN_SEPARATOR = keccak256(
    abi.encode(
        keccak256("EIP712Domain(string name,string version,uint256 chainId,address verifyingContract)"),
        keccak256(bytes("XRPShield FCC Extension")),
        keccak256(bytes("1")),
        114, // Flare Coston2 Testnet
        address(FCCExtensionAdapter)
    )
);
```

---

## 🏗️ 2. ActionResult Struct & TypeHash

```solidity
bytes32 public constant ACTION_RESULT_TYPEHASH = keccak256(
    "ActionResult(address vaultAddress,bytes32 policyHash,string status,bytes32 attestationHash,uint256 nonce,uint256 timestamp,uint256 deadline)"
);

struct ActionResult {
    bool success;
    string status;          // "APPROVED" or "REJECTED"
    string rationale;
    bytes32 policyHash;      // Canonical Keccak256 commitment hash
    bytes32 attestationHash; // Internal TEE attestation proof hash
    uint256 nonce;           // Monotonically increasing instruction nonce
    uint256 timestamp;       // Unix evaluation timestamp
    uint256 deadline;        // Unix execution expiration timestamp
    bytes signature;         // 65-byte ECDSA signature (v, r, s)
}
```

---

## 🛡️ 3. On-Chain Signature Recovery & Verification Flow

1. **Chain ID Verification**: Reverts if `block.chainid != 114` (Coston2 Testnet).
2. **Deadline Expiration Check**: Reverts if `block.timestamp > result.deadline`.
3. **Anti-Replay Nonce Check**: Reverts if `result.nonce <= vaultNonces[vaultAddress]`.
4. **Digest Calculation**: Computes `digest = keccak256("\x19\x01" || DOMAIN_SEPARATOR || structHash)`.
5. **Signer Recovery**: Executes `ecrecover(digest, v, r, s)` and validates against `extensionSignerAddress`.
6. **State Transition**: Sets `vaultNonces[vaultAddress] = result.nonce` and authorizes state transition `TEE_APPROVED`.
