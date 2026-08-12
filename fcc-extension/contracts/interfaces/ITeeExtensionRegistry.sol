// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

interface ITeeExtensionRegistry {
    event InstructionSubmitted(
        bytes32 indexed instructionId,
        address indexed sender,
        uint8 indexed operationType,
        bytes payload,
        uint256 timestamp
    );

    event ExtensionRegistered(
        bytes32 indexed extensionId,
        address indexed owner,
        string endpointUri,
        uint8 operationType
    );

    function registerExtension(
        bytes32 extensionId,
        string calldata endpointUri,
        uint8 operationType
    ) external;

    function submitInstruction(
        bytes32 extensionId,
        uint8 operationType,
        bytes calldata payload
    ) external returns (bytes32 instructionId);

    function getExtensionOwner(bytes32 extensionId) external view returns (address);
    function getExtensionEndpoint(bytes32 extensionId) external view returns (string memory);
}
