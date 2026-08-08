// SPDX-License-Identifier: MIT
pragma solidity 0.8.24;

/**
 * @title CommonErrors
 * @dev Custom error definitions for XRPShield Smart Contracts
 */
library CommonErrors {
    error UnauthorizedCaller(address caller);
    error InvalidVaultAddress(address vaultAddress);
    error VaultAlreadyRegistered(address vaultAddress);
    error VaultNotFound(address vaultAddress);
    error SystemPaused();
    error InvalidParameters();
    error ZeroAddressDetected();
}
