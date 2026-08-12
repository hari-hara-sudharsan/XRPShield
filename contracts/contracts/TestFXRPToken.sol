// SPDX-License-Identifier: MIT
pragma solidity 0.8.24;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

/**
 * @title TestFXRPToken
 * @dev ERC-20 Token representing Flare Wrapped XRP (FXRP) reserves on Flare Coston2 Testnet.
 */
contract TestFXRPToken is ERC20, Ownable {
    uint8 private constant DECIMALS = 18;

    event TokensMinted(address indexed to, uint256 amount);

    constructor() ERC20("Flare Wrapped XRP", "FXRP") Ownable(msg.sender) {
        // Mint initial 1,000,000 FXRP supply to contract deployer
        _mint(msg.sender, 1_000_000 * 10**DECIMALS);
    }

    /**
     * @notice Mint testnet FXRP reserves to specified recipient (faucet function for testing)
     */
    function mint(address to, uint256 amount) external {
        _mint(to, amount);
        emit TokensMinted(to, amount);
    }

    function decimals() public pure override returns (uint8) {
        return DECIMALS;
    }
}
