// SPDX-License-Identifier: MIT
pragma solidity 0.8.24;

import "@openzeppelin/contracts/access/Ownable.sol";
import "./common/CommonErrors.sol";

/**
 * @title FCCExtensionAdapter
 * @dev On-chain contract for verifying EIP-712 Flare Confidential Compute (FCC) extension TEE attestation signatures and ActionResult payloads
 */
contract FCCExtensionAdapter is Ownable {
    address public extensionSignerAddress;
    uint256 public constant REQUIRED_CHAIN_ID = 114; // Flare Coston2 Testnet

    bytes32 public immutable DOMAIN_SEPARATOR;
    bytes32 public constant ACTION_RESULT_TYPEHASH = keccak256(
        "ActionResult(address vaultAddress,bytes32 policyHash,string status,bytes32 attestationHash,uint256 nonce,uint256 timestamp,uint256 deadline)"
    );

    struct ActionResult {
        bool success;
        string status;
        string rationale;
        bytes32 policyHash;
        bytes32 attestationHash;
        uint256 nonce;
        uint256 timestamp;
        uint256 deadline;
        bytes signature;
    }

    mapping(address => uint256) public vaultNonces;
    mapping(bytes32 => bool) public verifiedAttestations;

    event ExtensionSignerUpdated(address indexed newSigner);
    event ActionResultVerified(address indexed vaultAddress, bytes32 indexed policyHash, string status, bool verified);

    constructor(address _extensionSigner) Ownable(msg.sender) {
        extensionSignerAddress = _extensionSigner != address(0) ? _extensionSigner : msg.sender;

        DOMAIN_SEPARATOR = keccak256(
            abi.encode(
                keccak256("EIP712Domain(string name,string version,uint256 chainId,address verifyingContract)"),
                keccak256(bytes("XRPShield FCC Extension")),
                keccak256(bytes("1")),
                block.chainid == 31337 ? 31337 : REQUIRED_CHAIN_ID,
                address(this)
            )
        );
    }

    function setExtensionSigner(address _signer) external onlyOwner {
        if (_signer == address(0)) revert CommonErrors.ZeroAddressDetected();
        extensionSignerAddress = _signer;
        emit ExtensionSignerUpdated(_signer);
    }

    /**
     * @notice Verifies cryptographic EIP-712 TEE signature of an ActionResult payload with full domain, chain, nonce, and deadline enforcement
     */
    function verifyAndRecordAttestation(address vaultAddress, ActionResult calldata result) public returns (bool) {
        if (block.chainid != REQUIRED_CHAIN_ID && block.chainid != 31337) return false;
        if (!result.success || result.signature.length != 65) return false;
        if (result.deadline > 0 && block.timestamp > result.deadline) return false;
        if (result.timestamp > block.timestamp + 300) return false; // Prevent future timestamps
        if (result.nonce <= vaultNonces[vaultAddress]) return false; // Anti-replay nonce check

        bytes32 structHash = keccak256(
            abi.encode(
                ACTION_RESULT_TYPEHASH,
                vaultAddress,
                result.policyHash,
                keccak256(bytes(result.status)),
                result.attestationHash,
                result.nonce,
                result.timestamp,
                result.deadline
            )
        );

        bytes32 digest = keccak256(
            abi.encodePacked(
                "\x19\x01",
                DOMAIN_SEPARATOR,
                structHash
            )
        );

        address recoveredSigner = recoverSigner(digest, result.signature);
        if (recoveredSigner != extensionSignerAddress) return false;

        vaultNonces[vaultAddress] = result.nonce;
        verifiedAttestations[result.attestationHash] = true;

        emit ActionResultVerified(vaultAddress, result.policyHash, result.status, true);
        return true;
    }

    /**
     * @notice Pure read-only view method to verify an attestation without modifying state
     */
    function isAttestationValid(address vaultAddress, ActionResult calldata result) public view returns (bool) {
        if (block.chainid != REQUIRED_CHAIN_ID && block.chainid != 31337) return false;
        if (!result.success || result.signature.length != 65) return false;
        if (result.deadline > 0 && block.timestamp > result.deadline) return false;
        if (result.timestamp > block.timestamp + 300) return false;
        if (result.nonce <= vaultNonces[vaultAddress]) return false;

        bytes32 structHash = keccak256(
            abi.encode(
                ACTION_RESULT_TYPEHASH,
                vaultAddress,
                result.policyHash,
                keccak256(bytes(result.status)),
                result.attestationHash,
                result.nonce,
                result.timestamp,
                result.deadline
            )
        );

        bytes32 digest = keccak256(
            abi.encodePacked(
                "\x19\x01",
                DOMAIN_SEPARATOR,
                structHash
            )
        );

        return recoverSigner(digest, result.signature) == extensionSignerAddress;
    }

    function recoverSigner(bytes32 _digest, bytes memory _signature) public pure returns (address) {
        if (_signature.length != 65) return address(0);
        bytes32 r;
        bytes32 s;
        uint8 v;
        assembly {
            r := mload(add(_signature, 32))
            s := mload(add(_signature, 64))
            v := byte(0, mload(add(_signature, 96)))
        }
        if (v < 27) v += 27;
        if (v != 27 && v != 28) return address(0);
        return ecrecover(_digest, v, r, s);
    }

    function verifyAttestationView(address vaultAddress, ActionResult calldata result) external view returns (bool) {
        return isAttestationValid(vaultAddress, result);
    }
}
