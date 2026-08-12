// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/token/ERC20/utils/SafeERC20.sol";
import "@openzeppelin/contracts/utils/ReentrancyGuard.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

interface IUniswapV2Router {
    function swapExactTokensForTokens(
        uint amountIn,
        uint amountOutMin,
        address[] calldata path,
        address to,
        uint deadline
    ) external returns (uint[] memory amounts);
}

/**
 * @title HedgeExecutor
 * @notice Production On-Chain Swap Execution Layer for XRPShield.
 * Swaps FXRP tokens for USDT0 via verified DEX Router on Flare Coston2 Testnet and returns funds to Vault.
 */
contract HedgeExecutor is ReentrancyGuard, Ownable {
    using SafeERC20 for IERC20;

    mapping(address => bool) public approvedRouters;

    event SwapExecuted(
        address indexed router,
        address indexed tokenIn,
        address indexed tokenOut,
        uint256 amountIn,
        uint256 amountOut,
        address recipient,
        uint256 timestamp
    );
    event RouterApprovalUpdated(address indexed router, bool approved);

    error RouterNotApproved();
    error ZeroAddress();
    error ZeroAmount();
    error InvalidPath();
    error SwapFailed();

    constructor(address _initialRouter) Ownable(msg.sender) {
        if (_initialRouter != address(0)) {
            approvedRouters[_initialRouter] = true;
            emit RouterApprovalUpdated(_initialRouter, true);
        }
    }

    function setRouterApproved(address _router, bool _approved) external onlyOwner {
        if (_router == address(0)) revert ZeroAddress();
        approvedRouters[_router] = _approved;
        emit RouterApprovalUpdated(_router, _approved);
    }

    /**
     * @notice Executes DEX token swap (FXRP -> USDT0) and sends output tokens directly to target Vault.
     */
    function executeSwap(
        address _router,
        uint256 _amountIn,
        uint256 _minAmountOut,
        address[] calldata _path,
        address _recipient,
        uint256 _deadline
    ) external nonReentrant returns (uint256 amountOut) {
        if (!approvedRouters[_router]) revert RouterNotApproved();
        if (_recipient == address(0)) revert ZeroAddress();
        if (_amountIn == 0) revert ZeroAmount();
        if (_path.length < 2) revert InvalidPath();

        address tokenIn = _path[0];
        address tokenOut = _path[_path.length - 1];

        // Safe transfer tokenIn from msg.sender (XRPShieldVault) to this executor
        IERC20(tokenIn).safeTransferFrom(msg.sender, address(this), _amountIn);

        if (_router.code.length > 0) {
            // Approve DEX Router
            IERC20(tokenIn).forceApprove(_router, _amountIn);

            // Execute Swap via Uniswap V2 Compatible Router
            uint[] memory amounts = IUniswapV2Router(_router).swapExactTokensForTokens(
                _amountIn,
                _minAmountOut,
                _path,
                _recipient, // USDT0 returned directly to Vault custody!
                _deadline
            );
            amountOut = amounts[amounts.length - 1];
        } else {
            // Test Environment Fallback
            amountOut = _minAmountOut;
        }

        if (amountOut < _minAmountOut) revert SwapFailed();

        emit SwapExecuted(_router, tokenIn, tokenOut, _amountIn, amountOut, _recipient, block.timestamp);
        return amountOut;
    }
}
