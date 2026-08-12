// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "./interfaces/ITeeExtensionRegistry.sol";

/**
 * @title XRPShieldInstructionSender
 * @notice Official Flare Confidential Compute (FCC) Instruction Sender for XRPShield.
 * Dispatches policy evaluation instructions to TeeExtensionRegistry on Flare Coston2 Testnet.
 */
contract XRPShieldInstructionSender {
    ITeeExtensionRegistry public immutable registry;
    bytes32 public immutable extensionId;
    uint8 public constant OP_TYPE_XRP_SHIELD = 42;

    event PolicyInstructionSent(
        bytes32 indexed instructionId,
        address indexed vaultAddress,
        bytes32 indexed policyCommitment,
        uint256 timestamp
    );

    error ZeroAddressDetected();
    error InvalidInstruction();

    constructor(address _registryAddress, bytes32 _extensionId) {
        if (_registryAddress == address(0)) revert ZeroAddressDetected();
        registry = ITeeExtensionRegistry(_registryAddress);
        extensionId = _extensionId;
    }

    /**
     * @notice Submits confidential policy evaluation request to Flare TEE infrastructure.
     */
    function sendEvaluatePolicyInstruction(
        address vaultAddress,
        bytes32 policyCommitment,
        uint256 currentXrpPrice,
        uint256 deadline
    ) external returns (bytes32 instructionId) {
        bytes memory payload = abi.encode(
            "EVALUATE_POLICY",
            vaultAddress,
            policyCommitment,
            currentXrpPrice,
            deadline
        );

        instructionId = registry.submitInstruction(extensionId, OP_TYPE_XRP_SHIELD, payload);
        emit PolicyInstructionSent(instructionId, vaultAddress, policyCommitment, block.timestamp);
        return instructionId;
    }

    /**
     * @notice Submits status query instruction to TEE extension handler.
     */
    function sendGetStatusInstruction() external returns (bytes32 instructionId) {
        bytes memory payload = abi.encode("GET_STATUS");
        return registry.submitInstruction(extensionId, OP_TYPE_XRP_SHIELD, payload);
    }
}
