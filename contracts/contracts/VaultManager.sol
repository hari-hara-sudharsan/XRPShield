// SPDX-License-Identifier: MIT
pragma solidity 0.8.24;

import "@openzeppelin/contracts/utils/Pausable.sol";
import "@openzeppelin/contracts/utils/ReentrancyGuard.sol";
import "./interfaces/IVaultManager.sol";
import "./AccessManager.sol";
import "./TreasuryStorage.sol";
import "./common/CommonErrors.sol";

/**
 * @title VaultManager
 * @dev Core production vault registry, policy commitment, decision tracking, and protected execution engine on Flare Network
 */
contract VaultManager is IVaultManager, Pausable, ReentrancyGuard {
    AccessManager public immutable accessManager;
    TreasuryStorage public immutable storageContract;

    modifier onlyOperator() {
        if (!accessManager.isOperator(msg.sender)) revert CommonErrors.UnauthorizedCaller(msg.sender);
        _;
    }

    modifier onlyPauser() {
        if (!accessManager.isPauser(msg.sender)) revert CommonErrors.UnauthorizedCaller(msg.sender);
        _;
    }

    constructor(address accessManagerAddress, address storageAddress) {
        if (accessManagerAddress == address(0) || storageAddress == address(0)) {
            revert CommonErrors.ZeroAddressDetected();
        }
        accessManager = AccessManager(accessManagerAddress);
        storageContract = TreasuryStorage(storageAddress);
    }

    function pause() external onlyPauser {
        _pause();
    }

    function unpause() external onlyPauser {
        _unpause();
    }

    function registerVault(
        address vaultAddress,
        string calldata name,
        string calldata assetType
    ) external override whenNotPaused nonReentrant {
        if (vaultAddress == address(0)) revert CommonErrors.ZeroAddressDetected();
        if (bytes(name).length == 0) revert CommonErrors.InvalidParameters();

        VaultInfo memory existing = storageContract.getVault(vaultAddress);
        if (existing.vaultAddress != address(0)) {
            revert CommonErrors.VaultAlreadyRegistered(vaultAddress);
        }

        VaultInfo memory newVault = VaultInfo({
            vaultAddress: vaultAddress,
            owner: msg.sender,
            name: name,
            assetType: bytes(assetType).length > 0 ? assetType : "FXRP",
            status: VaultStatus.ACTIVE,
            registeredAt: block.timestamp
        });

        storageContract.setVault(vaultAddress, newVault);
        emit VaultRegistered(vaultAddress, msg.sender, name, block.timestamp);
    }

    function updateVaultStatus(
        address vaultAddress,
        VaultStatus newStatus
    ) external override whenNotPaused nonReentrant {
        VaultInfo memory vault = storageContract.getVault(vaultAddress);
        if (vault.vaultAddress == address(0)) {
            revert CommonErrors.VaultNotFound(vaultAddress);
        }
        if (vault.owner != msg.sender && !accessManager.isOperator(msg.sender)) {
            revert CommonErrors.UnauthorizedCaller(msg.sender);
        }

        vault.status = newStatus;
        storageContract.setVault(vaultAddress, vault);
        emit VaultUpdated(vaultAddress, newStatus, block.timestamp);
    }

    function deposit(address vaultAddress) external payable override whenNotPaused nonReentrant {
        if (msg.value == 0) revert CommonErrors.InvalidParameters();

        VaultInfo memory vault = storageContract.getVault(vaultAddress);
        if (vault.vaultAddress == address(0)) {
            revert CommonErrors.VaultNotFound(vaultAddress);
        }
        if (vault.status != VaultStatus.ACTIVE) {
            revert CommonErrors.SystemPaused();
        }

        uint256 currentBalance = storageContract.getBalance(vaultAddress);
        uint256 newBalance = currentBalance + msg.value;
        storageContract.setBalance(vaultAddress, newBalance);

        emit VaultDeposited(vaultAddress, msg.sender, msg.value, block.timestamp);
    }

    function withdraw(address vaultAddress, uint256 amount) external override whenNotPaused nonReentrant {
        if (amount == 0) revert CommonErrors.InvalidParameters();

        VaultInfo memory vault = storageContract.getVault(vaultAddress);
        if (vault.vaultAddress == address(0)) {
            revert CommonErrors.VaultNotFound(vaultAddress);
        }
        if (vault.owner != msg.sender && !accessManager.isOperator(msg.sender)) {
            revert CommonErrors.UnauthorizedCaller(msg.sender);
        }
        if (vault.status != VaultStatus.ACTIVE) {
            revert CommonErrors.SystemPaused();
        }

        uint256 currentBalance = storageContract.getBalance(vaultAddress);
        if (currentBalance < amount) {
            revert CommonErrors.InvalidParameters();
        }

        uint256 newBalance = currentBalance - amount;
        storageContract.setBalance(vaultAddress, newBalance);

        (bool success, ) = msg.sender.call{value: amount}("");
        if (!success) {
            revert CommonErrors.InvalidParameters();
        }

        emit VaultWithdrawn(vaultAddress, msg.sender, amount, block.timestamp);
    }

    function registerPolicyCommitment(
        address vaultAddress,
        bytes32 policyHash,
        string calldata metadataUri
    ) external override whenNotPaused nonReentrant {
        if (policyHash == bytes32(0)) revert CommonErrors.InvalidParameters();

        VaultInfo memory vault = storageContract.getVault(vaultAddress);
        if (vault.vaultAddress == address(0)) {
            revert CommonErrors.VaultNotFound(vaultAddress);
        }
        if (vault.owner != msg.sender && !accessManager.isOperator(msg.sender)) {
            revert CommonErrors.UnauthorizedCaller(msg.sender);
        }

        storageContract.setPolicyCommitment(vaultAddress, policyHash);
        emit PolicyCommitmentRegistered(vaultAddress, policyHash, metadataUri, block.timestamp);
    }

    function recordPolicyAttestation(
        address vaultAddress,
        bytes32 policyHash,
        string calldata attestationId,
        bool status
    ) external override whenNotPaused onlyOperator nonReentrant {
        if (bytes(attestationId).length == 0) revert CommonErrors.InvalidParameters();

        emit PolicyAttestationRecorded(vaultAddress, policyHash, attestationId, status, block.timestamp);
    }

    function registerDecision(
        address vaultAddress,
        bytes32 decisionHash,
        string calldata decisionType,
        string calldata metadataUri
    ) external override whenNotPaused nonReentrant {
        if (decisionHash == bytes32(0)) revert CommonErrors.InvalidParameters();

        VaultInfo memory vault = storageContract.getVault(vaultAddress);
        if (vault.vaultAddress == address(0)) {
            revert CommonErrors.VaultNotFound(vaultAddress);
        }
        if (vault.owner != msg.sender && !accessManager.isOperator(msg.sender)) {
            revert CommonErrors.UnauthorizedCaller(msg.sender);
        }

        storageContract.setDecisionHash(vaultAddress, decisionHash);
        emit DecisionRegistered(vaultAddress, decisionHash, decisionType, metadataUri, block.timestamp);
    }

    function updateDecisionStatus(
        address vaultAddress,
        bytes32 decisionHash,
        string calldata status
    ) external override whenNotPaused nonReentrant {
        if (decisionHash == bytes32(0)) revert CommonErrors.InvalidParameters();

        VaultInfo memory vault = storageContract.getVault(vaultAddress);
        if (vault.vaultAddress == address(0)) {
            revert CommonErrors.VaultNotFound(vaultAddress);
        }
        if (vault.owner != msg.sender && !accessManager.isOperator(msg.sender)) {
            revert CommonErrors.UnauthorizedCaller(msg.sender);
        }

        emit DecisionStatusUpdated(vaultAddress, decisionHash, status, block.timestamp);
    }

    function registerExecution(
        address vaultAddress,
        bytes32 decisionHash,
        bytes32 executionHash,
        string calldata executionState
    ) external override whenNotPaused nonReentrant {
        if (executionHash == bytes32(0)) revert CommonErrors.InvalidParameters();

        VaultInfo memory vault = storageContract.getVault(vaultAddress);
        if (vault.vaultAddress == address(0)) {
            revert CommonErrors.VaultNotFound(vaultAddress);
        }
        if (vault.owner != msg.sender && !accessManager.isOperator(msg.sender)) {
            revert CommonErrors.UnauthorizedCaller(msg.sender);
        }

        storageContract.setExecutionHash(vaultAddress, executionHash);
        emit ExecutionRegistered(vaultAddress, decisionHash, executionHash, executionState, block.timestamp);
    }

    function recordExecutionResult(
        address vaultAddress,
        bytes32 executionHash,
        string calldata resultPayload,
        bool success
    ) external override whenNotPaused onlyOperator nonReentrant {
        if (executionHash == bytes32(0)) revert CommonErrors.InvalidParameters();

        emit ExecutionResultRecorded(vaultAddress, executionHash, resultPayload, success, block.timestamp);
    }

    function getVault(address vaultAddress) external view override returns (VaultInfo memory) {
        VaultInfo memory vault = storageContract.getVault(vaultAddress);
        if (vault.vaultAddress == address(0)) {
            revert CommonErrors.VaultNotFound(vaultAddress);
        }
        return vault;
    }

    function getVaultBalance(address vaultAddress) external view override returns (uint256) {
        return storageContract.getBalance(vaultAddress);
    }

    function getLatestPolicyCommitment(address vaultAddress) external view override returns (bytes32) {
        return storageContract.getPolicyCommitment(vaultAddress);
    }

    function getLatestDecisionHash(address vaultAddress) external view override returns (bytes32) {
        return storageContract.getDecisionHash(vaultAddress);
    }

    function getLatestExecutionHash(address vaultAddress) external view override returns (bytes32) {
        return storageContract.getExecutionHash(vaultAddress);
    }

    function isVaultActive(address vaultAddress) external view override returns (bool) {
        VaultInfo memory vault = storageContract.getVault(vaultAddress);
        return vault.vaultAddress != address(0) && vault.status == VaultStatus.ACTIVE;
    }

    receive() external payable {}
}
