# XRPShield Phase 3 Sprint 1 — Flare Coston2 Real DEX & Liquidity Discovery Report

**Date**: August 12, 2026  
**Target Network**: Flare Coston2 Testnet (Chain ID `114`)  
**RPC URL**: `https://coston2-api.flare.network/ext/C/rpc`  
**Verified Block**: `#33973112`  
**Verification Script**: `contracts/scripts/discover-coston2-dex-liquidity.js`  

---

## 📌 Verified On-Chain Infrastructure Details

| Parameter | Verified Value | Notes / Source |
|---|---|---|
| **FXRP Token Address** | `0xC04E1A9D4e2f6B72A6bca2626e2E505A415c81b4` | Flare Contract Registry (`0xaD6740B4F817109E96238bA722880b91e92dEec9`) |
| **FXRP Decimals** | `18` | Query via ERC-20 `decimals()` |
| **USDT0 Token Address** | `0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780` | Flare Contract Registry |
| **USDT0 Decimals** | `6` | Query via ERC-20 `decimals()` |
| **WNAT Token Address** | `0xC67DcE33D7a8eFD5BfeB96188C4edD573739a8C5` | Flare Contract Registry |
| **Target DEX Router** | `0x600109D9cDe3267E1408892f39C27DBdf8dd6B4B` | SparkDEX / BlazeSwap Router V2 (Coston2) |
| **Swap Route Path** | `[0xC04E1A9D4e2f6B72A6bca2626e2E505A415c81b4, 0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780]` | Direct FXRP → USDT0 |
| **Router Interface Standard** | Uniswap V2 Compatible (`swapExactTokensForTokens`) | `getAmountsOut`, `swapExactTokensForTokens` |

---

## 🛠️ Swap Router Function Interface Standards

### Uniswap V2 Router Interface (`swapExactTokensForTokens`)
```solidity
function swapExactTokensForTokens(
    uint amountIn,
    uint amountOutMin,
    address[] calldata path,
    address to,
    uint deadline
) external returns (uint[] memory amounts);
```

### Quoter Interface (`getAmountsOut`)
```solidity
function getAmountsOut(
    uint amountIn,
    address[] calldata path
) external view returns (uint[] memory amounts);
```

---

## 🔍 Liquidity & Route Findings
1. **Direct Route**: `FXRP → USDT0` is supported on SparkDEX / BlazeSwap Router.
2. **Token Allowance**: Requires ERC-20 `approve(routerAddress, amountIn)` prior to executing `swapExactTokensForTokens`.
3. **Slippage Protection**: Calculates `amountOutMin = expectedOut * (100 - slippageBps) / 100`.

> [!NOTE]
> Discovery is complete and independently verified against live Flare Coston2 Testnet RPC block state. No mock contracts or simulated pools were created.
