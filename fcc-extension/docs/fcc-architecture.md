# Flare Confidential Compute (FCC) Architecture — XRPShield

## 📌 Architecture Pipeline

```text
XRPShield InstructionSender
        ↓
TeeExtensionRegistry (0x8A791620dd6260079BF849Dc5567aDC3F2FdC318)
        ↓
Flare TEE Infrastructure
        ↓
ext-proxy (Port 3000)
        ↓
XRPShield extension handler (Port 8080)
        ↓
TEE ActionResult (EIP-712 Signed)
        ↓
Flare Coston2 Testnet (Chain ID 114)
```

## 🛠️ Environment Configuration

| Variable | Description | Value |
|---|---|---|
| `FLARE_COSTON2_RPC_URL` | Coston2 EVM RPC Endpoint | `https://coston2-api.flare.network/ext/C/rpc` |
| `FLARE_CHAIN_ID` | Flare Coston2 Chain Identifier | `114` (`0x72`) |
| `EXTENSION_ID` | 32-Byte Hex Extension ID | `0x585250536869656c64464343457874656e73696f6e0000000000000000000001` |
| `OPERATION_TYPE` | Operation Identifier (`OP_TYPE_XRP_SHIELD`) | `42` |
| `TEE_REGISTRY_ADDRESS` | Coston2 TeeExtensionRegistry Address | `0x8A791620dd6260079BF849Dc5567aDC3F2FdC318` |
| `SIMULATED_TEE` | Development Mode Flag | `true` (Explicitly labeled for local dev) |

## 🚀 Commands Supported

- **`EVALUATE_POLICY`**: Evaluates confidential hedge rules inside TEE enclave and signs an EIP-712 `ActionResult`.
- **`GET_STATUS`**: Returns extension operational health status.
