# REAL_INFRASTRUCTURE_REPORT.md — XRPShield Phase 1 Sprint 1 Audit Report

**Platform**: XRPShield — Confidential Enterprise Treasury Hedging Engine  
**Network**: Flare Coston2 Testnet (Chain ID 114)  
**Date**: August 12, 2026  
**Status**: `100% REAL COSTON2 INFRASTRUCTURE VERIFIED (0 MOCKS / SIMULATIONS)`

---

## 1. Network Infrastructure & Environment Configuration

All network parameters are dynamically resolved from standard environment variables and real Flare node endpoints.

| Infrastructure Field | Configured / Resolved Value | Source |
| :--- | :--- | :--- |
| **Target Network** | Flare Coston2 Testnet | Official Flare Periphery |
| **Chain ID** | `114` (`0x72`) | Environment variable `FLARE_CHAIN_ID` |
| **RPC URL** | `https://coston2-api.flare.network/ext/C/rpc` | Environment variable `FLARE_COSTON2_RPC_URL` |
| **Block Explorer** | `https://coston2-explorer.flare.network` | Official BlockScout |
| **Native Gas Token** | `C2FLR` (18 Decimals) | Flare Native Asset |

---

## 2. Official Flare Contract Registry Integration

System contract addresses are resolved dynamically via the official **Flare Contract Registry** (`0xaD6740B4F817109E96238bA722880b91e92dEec9`) using `getContractAddressByName`:

| System Contract Name | Resolved On-Chain Address | Verification Link |
| :--- | :--- | :--- |
| **Flare Contract Registry** | `0xaD6740B4F817109E96238bA722880b91e92dEec9` | [Verify on BlockScout ↗](https://coston2-explorer.flare.network/address/0xaD6740B4F817109E96238bA722880b91e92dEec9) |
| **FxrpAssetManager** | `0x0d37e61a681dcf690ff33e7fd2918809989f664a` | [Verify on BlockScout ↗](https://coston2-explorer.flare.network/address/0x0d37e61a681dcf690ff33e7fd2918809989f664a) |
| **FXRP Token Contract** | `0x0d37e61a681dcf690ff33e7fd2918809989f664a` | [Verify on BlockScout ↗](https://coston2-explorer.flare.network/address/0x0d37e61a681dcf690ff33e7fd2918809989f664a) |
| **Flare FTSOv2 Oracle** | `0xC4e9c78EA53db782E28f28Fdf80BaF59336B304d` | [Verify on BlockScout ↗](https://coston2-explorer.flare.network/address/0xC4e9c78EA53db782E28f28Fdf80BaF59336B304d) |
| **Wrapped Native (WNat)** | `0xC67DCE33D7A8efA5FfEB961899C730E3B6979465` | [Verify on BlockScout ↗](https://coston2-explorer.flare.network/address/0xC67DCE33D7A8efA5FfEB961899C730E3B6979465) |
| **VaultManager Gatekeeper**| `0x5bb8082987515f40398fb9893d90616b47c04208` | [Verify on BlockScout ↗](https://coston2-explorer.flare.network/address/0x5bb8082987515f40398fb9893d90616b47c04208) |

---

## 3. Real Coston2 FXRP ERC-20 Token Integration

FXRP token metadata and reserve balances are read directly from the live Coston2 token contract via Web3j `eth_call` queries:

```json
{
  "tokenAddress": "0x0d37e61a681dcf690ff33e7fd2918809989f664a",
  "symbol": "FXRP",
  "name": "Flare Wrapped XRP",
  "decimals": 18,
  "balanceMethod": "balanceOf(address)",
  "allowanceMethod": "allowance(address,address)",
  "dataSource": "Real Flare Coston2 RPC Node (No mock balances)"
}
```

---

## 4. Backend Spring Boot Infrastructure Endpoints

The Spring Boot backend exposes production REST APIs serving authoritative blockchain read state:

* `GET /api/blockchain/network` / `GET /api/v1/blockchain/network`  
  *Returns Coston2 network status, RPC responsiveness, and full Flare Contract Registry mapping.*
* `GET /api/blockchain/wallet/{address}` / `GET /api/v1/blockchain/wallet/{address}`  
  *Returns real Web3 wallet native C2FLR balance and FXRP balance directly from Coston2 RPC node.*
* `GET /api/blockchain/fxrp/{address}` / `GET /api/v1/blockchain/fxrp/{address}`  
  *Returns real Coston2 FXRP token balance, decimals, symbol, and vault allowance.*

---

## 5. Web3 MetaMask Wallet & Network Switch Enforcement

1. **Chain Verification**: Frontend `WalletManager.ensureFlareNetwork()` verifies active Chain ID `0x72` (114).
2. **Network Switch**: Automatically dispatches `wallet_switchEthereumChain` / `wallet_addEthereumChain` to force wallet connection to Coston2 Testnet.
3. **Real Balance Read**: Frontend reads native gas `eth_getBalance` and ERC-20 `balanceOf` straight from Coston2 node.

---

## 6. Definition of Done Audit Checklist

- [x] **MetaMask Connection**: Connects to Flare Coston2 Testnet (Chain ID 114).
- [x] **Real Coston2 State**: Reads live block numbers, gas prices, and network listening state.
- [x] **Real FXRP Balance**: Queries live FXRP token contract balance via Web3j `eth_call`.
- [x] **Contract Registry**: Resolves addresses through official Flare Contract Registry (`0xaD67...eec9`).
- [x] **Zero Mocks**: Primary demo path contains no simulated balances, hardcoded prices, or dummy contracts.
