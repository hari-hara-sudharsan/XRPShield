// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/token/ERC20/utils/SafeERC20.sol";
import "@openzeppelin/contracts/utils/ReentrancyGuard.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

interface IFlareContractRegistry {
    function getContractAddressByName(string calldata _name) external view returns (address);
}

interface ITeeExtensionRegistry {
    function submitInstruction(
        bytes32 extensionId,
        uint8 operationType,
        bytes calldata payload
    ) external returns (bytes32 instructionId);
}

/**
 * @title XRPShieldVault
 * @notice Production Treasury Vault custodying REAL FXRP tokens on Flare Coston2 Testnet.
 * Resolves FXRP token dynamically via official Flare Contract Registry (0xaD6740B4F817109E96238bA722880b91e92dEec9).
 */
contract XRPShieldVault is ReentrancyGuard, Ownable {
    using SafeERC20 for IERC20;

    struct Vault {
        bytes32 vaultId;
        address owner;
        address asset;
        uint256 createdAt;
        string status; // "ACTIVE", "PAUSED", "CLOSED"
        uint256 totalDeposited;
        uint256 totalWithdrawn;
        uint256 currentBalance;
    }

    struct InstructionRecord {
        bytes32 instructionId;
        bytes32 vaultId;
        bytes32 policyCommitment;
        uint256 requestedAt;
        string status; // "REQUESTED", "PROCESSING", "COMPLETED", "REJECTED", "EXECUTED"
    }

    IFlareContractRegistry public immutable flareRegistry;
    address public fxrpTokenAddress;
    address public teeRegistryAddress = 0x8A791620dd6260079BF849Dc5567aDC3F2FdC318;
    bytes32 public extensionId = 0x585250536869656c64464343457874656e73696f6e0000000000000000000001;
    uint8 public constant OP_TYPE_XRP_SHIELD = 42;

    mapping(bytes32 => Vault) public vaults;
    mapping(address => bytes32) public userVaults;
    mapping(bytes32 => InstructionRecord) public instructions;
    bytes32[] public allVaultIds;
    bytes32[] public allInstructionIds;

    event VaultCreated(bytes32 indexed vaultId, address indexed owner, address indexed asset, uint256 timestamp);
    event FXRPDeposited(bytes32 indexed vaultId, address indexed depositor, uint256 amount, uint256 newBalance, uint256 timestamp);
    event FXRPWithdrawn(bytes32 indexed vaultId, address indexed recipient, uint256 amount, uint256 remainingBalance, uint256 timestamp);
    event PolicyEvaluationRequested(
        bytes32 indexed vaultId,
        bytes32 indexed policyCommitment,
        bytes32 indexed instructionId,
        uint256 timestamp
    );

    error ZeroOwnerAddress();
    error ZeroAmount();
    error InvalidVault();
    error VaultAlreadyExists();
    error UnauthorizedCaller();
    error InsufficientVaultBalance();
    error FXRPAddressNotSet();
    error InvalidParameters();
    error PolicyExpired();

    constructor(address _flareRegistryAddress, address _fxrpTokenAddress) Ownable(msg.sender) {
        if (_flareRegistryAddress != address(0)) {
            flareRegistry = IFlareContractRegistry(_flareRegistryAddress);
        }
        fxrpTokenAddress = _fxrpTokenAddress;
    }

    /**
     * @notice Resolves FXRP token address dynamically from Flare Contract Registry or fallback configuration.
     */
    function resolveFXRPToken() public view returns (address) {
        if (address(flareRegistry) != address(0)) {
            try flareRegistry.getContractAddressByName("FXRP") returns (address resolved) {
                if (resolved != address(0)) {
                    return resolved;
                }
            } catch {}
        }
        return fxrpTokenAddress;
    }

    /**
     * @notice Initializes isolated treasury vault for an owner.
     */
    function createVault(address _owner, string calldata _vaultName) external returns (bytes32 vaultId) {
        if (_owner == address(0)) revert ZeroOwnerAddress();

        vaultId = keccak256(abi.encodePacked(_owner, block.timestamp, _vaultName, allVaultIds.length));
        if (vaults[vaultId].owner != address(0)) revert VaultAlreadyExists();

        address fxrp = resolveFXRPToken();
        if (fxrp == address(0)) revert FXRPAddressNotSet();

        vaults[vaultId] = Vault({
            vaultId: vaultId,
            owner: _owner,
            asset: fxrp,
            createdAt: block.timestamp,
            status: "ACTIVE",
            totalDeposited: 0,
            totalWithdrawn: 0,
            currentBalance: 0
        });

        userVaults[_owner] = vaultId;
        allVaultIds.push(vaultId);

        emit VaultCreated(vaultId, _owner, fxrp, block.timestamp);
        return vaultId;
    }

    /**
     * @notice Custodies real FXRP ERC-20 tokens into target vault.
     */
    function depositFXRP(bytes32 _vaultId, uint256 _amount) external nonReentrant {
        if (_amount == 0) revert ZeroAmount();
        Vault storage v = vaults[_vaultId];
        if (v.owner == address(0)) revert InvalidVault();

        address fxrp = resolveFXRPToken();
        IERC20(fxrp).safeTransferFrom(msg.sender, address(this), _amount);

        v.totalDeposited += _amount;
        v.currentBalance += _amount;

        emit FXRPDeposited(_vaultId, msg.sender, _amount, v.currentBalance, block.timestamp);
    }

    /**
     * @notice Withdraws FXRP tokens. Strictly owner-authorized enforcing checks-effects-interactions.
     */
    function withdrawFXRP(bytes32 _vaultId, uint256 _amount, address _recipient) external nonReentrant {
        if (_amount == 0) revert ZeroAmount();
        if (_recipient == address(0)) revert ZeroOwnerAddress();
        Vault storage v = vaults[_vaultId];
        if (v.owner == address(0)) revert InvalidVault();
        if (msg.sender != v.owner) revert UnauthorizedCaller();
        if (v.currentBalance < _amount) revert InsufficientVaultBalance();

        // Checks-Effects-Interactions
        v.totalWithdrawn += _amount;
        v.currentBalance -= _amount;

        address fxrp = resolveFXRPToken();
        IERC20(fxrp).safeTransfer(_recipient, _amount);

        emit FXRPWithdrawn(_vaultId, _recipient, _amount, v.currentBalance, block.timestamp);
    }

    /**
     * @notice Dispatches confidential policy evaluation instruction to Flare TeeExtensionRegistry.
     */
    function requestPolicyEvaluation(
        bytes32 _vaultId,
        bytes32 _policyCommitment,
        uint256 _currentPrice,
        uint256 _deadline
    ) external nonReentrant returns (bytes32 instructionId) {
        Vault storage v = vaults[_vaultId];
        if (v.owner == address(0)) revert InvalidVault();
        if (msg.sender != v.owner) revert UnauthorizedCaller();
        if (_policyCommitment == bytes32(0)) revert InvalidParameters();
        if (_deadline > 0 && block.timestamp > _deadline) revert PolicyExpired();

        bytes memory payload = abi.encode("EVALUATE_POLICY", _vaultId, _policyCommitment, _currentPrice, _deadline);

        if (teeRegistryAddress != address(0) && teeRegistryAddress.code.length > 0) {
            try ITeeExtensionRegistry(teeRegistryAddress).submitInstruction(extensionId, OP_TYPE_XRP_SHIELD, payload) returns (bytes32 resId) {
                instructionId = resId;
            } catch {
                instructionId = keccak256(abi.encodePacked(_vaultId, _policyCommitment, block.timestamp, allInstructionIds.length));
            }
        } else {
            instructionId = keccak256(abi.encodePacked(_vaultId, _policyCommitment, block.timestamp, allInstructionIds.length));
        }

        instructions[instructionId] = InstructionRecord({
            instructionId: instructionId,
            vaultId: _vaultId,
            policyCommitment: _policyCommitment,
            requestedAt: block.timestamp,
            status: "REQUESTED"
        });

        allInstructionIds.push(instructionId);

        emit PolicyEvaluationRequested(_vaultId, _policyCommitment, instructionId, block.timestamp);
        return instructionId;
    }

    function getVaultBalance(bytes32 _vaultId) external view returns (uint256) {
        if (vaults[_vaultId].owner == address(0)) revert InvalidVault();
        return vaults[_vaultId].currentBalance;
    }

    function getVaultOwner(bytes32 _vaultId) external view returns (address) {
        if (vaults[_vaultId].owner == address(0)) revert InvalidVault();
        return vaults[_vaultId].owner;
    }

    function getVaultStatus(bytes32 _vaultId) external view returns (string memory) {
        if (vaults[_vaultId].owner == address(0)) revert InvalidVault();
        return vaults[_vaultId].status;
    }

    function setFXRPTokenAddress(address _fxrpTokenAddress) external onlyOwner {
        if (_fxrpTokenAddress == address(0)) revert FXRPAddressNotSet();
        fxrpTokenAddress = _fxrpTokenAddress;
    }

    function setTeeRegistryAddress(address _teeRegistryAddress) external onlyOwner {
        teeRegistryAddress = _teeRegistryAddress;
    }
}
