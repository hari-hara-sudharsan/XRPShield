// SPDX-License-Identifier: MIT
pragma solidity 0.8.24;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

/**
 * @title TestUSDT0Token
 * @dev ERC-20 contract representing Flare Coston2 USD₮0 Omnichain Stablecoin (6 decimals)
 */
contract TestUSDT0Token is ERC20, Ownable {
    uint8 private constant DECIMALS = 6;

    constructor() ERC20("Testnet USDT0", "USDT0") Ownable(msg.sender) {
        _mint(msg.sender, 100000000 * 10**DECIMALS); // 100M USDT0 initial supply
    }

    function decimals() public pure override returns (uint8) {
        return DECIMALS;
    }

    function mint(address to, uint256 amount) external onlyOwner {
        _mint(to, amount);
    }
}
