// SPDX-License-Identifier: MIT
pragma solidity 0.8.24;

interface IVaultManager {
    enum VaultStatus { ACTIVE, PAUSED, CLOSED }

    struct VaultInfo {
        address vaultAddress;
        address owner;
        string name;
        string assetType;
        VaultStatus status;
        uint256 registeredAt;
    }

    event VaultRegistered(address indexed vaultAddress, address indexed owner, string name, uint256 timestamp);
    event VaultUpdated(address indexed vaultAddress, VaultStatus newStatus, uint256 timestamp);
    event VaultDeposited(address indexed vaultAddress, address indexed depositor, uint256 amount, uint256 timestamp);
    event VaultWithdrawn(address indexed vaultAddress, address indexed recipient, uint256 amount, uint256 timestamp);
    event PolicyCommitmentRegistered(address indexed vaultAddress, bytes32 indexed policyHash, string metadataUri, uint256 timestamp);
    event PolicyAttestationRecorded(address indexed vaultAddress, bytes32 indexed policyHash, string attestationId, bool status, uint256 timestamp);
    event DecisionRegistered(address indexed vaultAddress, bytes32 indexed decisionHash, string decisionType, string metadataUri, uint256 timestamp);
    event DecisionStatusUpdated(address indexed vaultAddress, bytes32 indexed decisionHash, string status, uint256 timestamp);
    event ExecutionRegistered(address indexed vaultAddress, bytes32 indexed decisionHash, bytes32 indexed executionHash, string executionState, uint256 timestamp);
    event ExecutionResultRecorded(address indexed vaultAddress, bytes32 indexed executionHash, string resultPayload, bool success, uint256 timestamp);

    function registerVault(address vaultAddress, string calldata name, string calldata assetType) external;
    function updateVaultStatus(address vaultAddress, VaultStatus newStatus) external;
    function deposit(address vaultAddress) external payable;
    function withdraw(address vaultAddress, uint256 amount) external;
    function registerPolicyCommitment(address vaultAddress, bytes32 policyHash, string calldata metadataUri) external;
    function recordPolicyAttestation(address vaultAddress, bytes32 policyHash, string calldata attestationId, bool status) external;
    function registerDecision(address vaultAddress, bytes32 decisionHash, string calldata decisionType, string calldata metadataUri) external;
    function updateDecisionStatus(address vaultAddress, bytes32 decisionHash, string calldata status) external;
    function registerExecution(address vaultAddress, bytes32 decisionHash, bytes32 executionHash, string calldata executionState) external;
    function recordExecutionResult(address vaultAddress, bytes32 executionHash, string calldata resultPayload, bool success) external;
    function getVault(address vaultAddress) external view returns (VaultInfo memory);
    function getVaultBalance(address vaultAddress) external view returns (uint256);
    function getLatestPolicyCommitment(address vaultAddress) external view returns (bytes32);
    function getLatestDecisionHash(address vaultAddress) external view returns (bytes32);
    function getLatestExecutionHash(address vaultAddress) external view returns (bytes32);
    function isVaultActive(address vaultAddress) external view returns (bool);
}
