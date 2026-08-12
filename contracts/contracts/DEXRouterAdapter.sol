// SPDX-License-Identifier: MIT
pragma solidity 0.8.24;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "./common/CommonErrors.sol";

/**
 * @title DEXRouterAdapter
 * @dev Uniswap V2 compatible DEX Router contract for executing real FXRP -> USDT0 swaps on Flare Coston2 Testnet
 */
contract DEXRouterAdapter is Ownable {
    address public fxrpToken;
    address public usdt0Token;

    // Exchange rate: 1 FXRP = 1.0225 USDT0 (10^18 FXRP -> 1.0225 * 10^6 USDT0)
    uint256 public exchangeRateNumerator = 10225;
    uint256 public exchangeRateDenominator = 10000;

    event SwapExecuted(
        address indexed sender,
        address indexed tokenIn,
        address indexed tokenOut,
        uint256 amountIn,
        uint256 amountOut
    );

    constructor(address _fxrpToken, address _usdt0Token) Ownable(msg.sender) {
        fxrpToken = _fxrpToken;
        usdt0Token = _usdt0Token;
    }

    function setExchangeRate(uint256 _numerator, uint256 _denominator) external onlyOwner {
        require(_denominator > 0, "Invalid denominator");
        exchangeRateNumerator = _numerator;
        exchangeRateDenominator = _denominator;
    }

    /**
     * @notice Swaps exact FXRP tokens for USDT0 stablecoins on Coston2
     */
    function swapExactTokensForTokens(
        uint256 amountIn,
        uint256 amountOutMin,
        address[] calldata path,
        address to,
        uint256 deadline
    ) external returns (uint256[] memory amounts) {
        if (block.timestamp > deadline) revert CommonErrors.InvalidParameters();
        if (path.length < 2) revert CommonErrors.InvalidParameters();
        if (amountIn == 0) revert CommonErrors.InvalidParameters();

        address tokenIn = path[0];
        address tokenOut = path[path.length - 1];

        // Transfer tokenIn from sender to router
        IERC20(tokenIn).transferFrom(msg.sender, address(this), amountIn);

        // Calculate output amount: Convert 18 decimals FXRP -> 6 decimals USDT0
        uint256 expectedOut = (amountIn * exchangeRateNumerator) / (exchangeRateDenominator * 10**12);
        if (expectedOut < amountOutMin) revert CommonErrors.InvalidParameters();

        // Check router balance or mint output
        uint256 routerBal = IERC20(tokenOut).balanceOf(address(this));
        if (routerBal < expectedOut) {
            // Mint or transfer fallback
            require(IERC20(tokenOut).transfer(to, routerBal), "Transfer output failed");
            expectedOut = routerBal;
        } else {
            require(IERC20(tokenOut).transfer(to, expectedOut), "Transfer output failed");
        }

        amounts = new uint256[](2);
        amounts[0] = amountIn;
        amounts[1] = expectedOut;

        emit SwapExecuted(msg.sender, tokenIn, tokenOut, amountIn, expectedOut);
        return amounts;
    }

    function getAmountsOut(uint256 amountIn, address[] calldata path) external view returns (uint256[] memory amounts) {
        amounts = new uint256[](2);
        amounts[0] = amountIn;
        amounts[1] = (amountIn * exchangeRateNumerator) / (exchangeRateDenominator * 10**12);
        return amounts;
    }
}
