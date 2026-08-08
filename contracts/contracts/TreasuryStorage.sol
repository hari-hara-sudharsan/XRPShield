// SPDX-License-Identifier: MIT
pragma solidity 0.8.24;

import "./interfaces/IVaultManager.sol";
import "./common/CommonErrors.sol";

/**
 * @title TreasuryStorage
 * @dev Separate persistent storage layout for vault metadata, balances, policy commitments, decisions, and execution tracking
 */
contract TreasuryStorage {
    address public managerContract;
    mapping(address => IVaultManager.VaultInfo) private _vaults;
    mapping(address => uint256) private _balances;
    mapping(address => bytes32) private _policyCommitments;
    mapping(address => bytes32) private _decisionHashes;
    mapping(address => bytes32) private _executionHashes;
    address[] private _allVaultAddresses;

    modifier onlyManager() {
        if (msg.sender != managerContract) revert CommonErrors.UnauthorizedCaller(msg.sender);
        _;
    }

    constructor() {
        managerContract = msg.sender;
    }

    function setManagerContract(address newManager) external {
        if (msg.sender != managerContract) revert CommonErrors.UnauthorizedCaller(msg.sender);
        if (newManager == address(0)) revert CommonErrors.ZeroAddressDetected();
        managerContract = newManager;
    }

    function setVault(address vaultAddress, IVaultManager.VaultInfo calldata info) external onlyManager {
        if (_vaults[vaultAddress].vaultAddress == address(0)) {
            _allVaultAddresses.push(vaultAddress);
        }
        _vaults[vaultAddress] = info;
    }

    function setBalance(address vaultAddress, uint256 newBalance) external onlyManager {
        _balances[vaultAddress] = newBalance;
    }

    function setPolicyCommitment(address vaultAddress, bytes32 policyHash) external onlyManager {
        _policyCommitments[vaultAddress] = policyHash;
    }

    function setDecisionHash(address vaultAddress, bytes32 decisionHash) external onlyManager {
        _decisionHashes[vaultAddress] = decisionHash;
    }

    function setExecutionHash(address vaultAddress, bytes32 executionHash) external onlyManager {
        _executionHashes[vaultAddress] = executionHash;
    }

    function getVault(address vaultAddress) external view returns (IVaultManager.VaultInfo memory) {
        return _vaults[vaultAddress];
    }

    function getBalance(address vaultAddress) external view returns (uint256) {
        return _balances[vaultAddress];
    }

    function getPolicyCommitment(address vaultAddress) external view returns (bytes32) {
        return _policyCommitments[vaultAddress];
    }

    function getDecisionHash(address vaultAddress) external view returns (bytes32) {
        return _decisionHashes[vaultAddress];
    }

    function getExecutionHash(address vaultAddress) external view returns (bytes32) {
        return _executionHashes[vaultAddress];
    }

    function getAllVaultsCount() external view returns (uint256) {
        return _allVaultAddresses.length;
    }
}
