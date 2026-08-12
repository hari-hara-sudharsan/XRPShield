# XRPShield Phase 2 — Real DEX Quote Engine & Slippage Model

---

## 📈 1. Live DEX Quote Querying Architecture

The Spring Boot `DexQuoteService.java` queries the SparkDEX Router V2 directly on Coston2 via Web3 RPC calls to `getAmountsOut`:

```solidity
function getAmountsOut(uint amountIn, address[] calldata path) external view returns (uint[] memory amounts);
```

---

## 🛡️ 2. Minimum Output & Slippage Formula

For a given input amount $A_{\text{in}}$, the quote engine computes the raw expected output $A_{\text{out}}$ and applies a maximum slippage tolerance $S = 0.5\%$:

$$\text{minimumAmountOut} = A_{\text{out}} \times \left(1 - \frac{S}{100}\right)$$

### Example Execution Calculation:
- **Input Token**: FXRP (`10.00 FXRP` = $10 \times 10^{18}$ wei)
- **Path**: `[0xC04E1A9D4e2f6B72A6bca2626e2E505A415c81b4, 0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780]`
- **Expected Output**: $8.4575 \text{ USDT0}$ ($8,457,500$ units, 6 decimals)
- **Slippage Cap ($0.5\%$)**: $8.415212 \text{ USDT0}$ ($8,415,212$ units)

```solidity
if (amountsOut[1] < minimumAmountOut) revert SwapFailed();
```
If market depth degrades and returns less than `8.415212 USDT0`, the smart contract transaction **REVERTS IMMEDIATELY**.
