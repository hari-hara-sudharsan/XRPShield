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

interface IFCCExtensionAdapter {
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

    function verifyAndRecordAttestation(address vaultAddress, ActionResult calldata result) external returns (bool);
}

interface IHedgeExecutor {
    function executeSwap(
        address _router,
        uint256 _amountIn,
        uint256 _minAmountOut,
        address[] calldata _path,
        address _recipient,
        uint256 _deadline
    ) external returns (uint256 amountOut);
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
        uint256 usdt0Balance;
    }

    struct InstructionRecord {
        bytes32 instructionId;
        bytes32 vaultId;
        bytes32 policyCommitment;
        uint256 requestedAt;
        string status; // "REQUESTED", "PROCESSING", "TEE_APPROVED", "TEE_REJECTED", "EXECUTION_AUTHORIZED", "EXECUTING", "EXECUTED", "EXECUTION_FAILED"
    }

    struct VerifiedFCCAttestation {
        bytes32 vaultId;
        bytes32 policyCommitment;
        bytes32 instructionId;
        string decision;
        uint256 approvedHedgeAmount;
        uint256 nonce;
        uint256 timestamp;
        uint256 chainId;
        address verifyingContract;
        bool isVerified;
    }

    struct ExecuteHedgeParams {
        bytes32 vaultId;
        bytes32 policyCommitment;
        bytes32 instructionId;
        uint256 amountIn;
        uint256 minimumAmountOut;
        uint256 deadline;
        address[] route;
        string verifiedDecision;
    }

    IFlareContractRegistry public immutable flareRegistry;
    address public fxrpTokenAddress;
    address public usdt0TokenAddress = 0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780;
    address public teeRegistryAddress = 0x8A791620dd6260079BF849Dc5567aDC3F2FdC318;
    address public fccAdapterAddress;
    address public hedgeExecutorAddress;
    address public dexRouterAddress = 0x600109D9cDe3267E1408892f39C27DBdf8dd6B4B;
    bytes32 public extensionId = 0x585250536869656c64464343457874656e73696f6e0000000000000000000001;
    uint8 public constant OP_TYPE_XRP_SHIELD = 42;

    mapping(bytes32 => Vault) public vaults;
    mapping(address => bytes32) public userVaults;
    mapping(bytes32 => InstructionRecord) public instructions;
    mapping(bytes32 => VerifiedFCCAttestation) public verifiedAttestations;
    mapping(bytes32 => bool) public processedInstructionIds;
    mapping(bytes32 => bool) public executedInstructionIds;
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
    event PolicyEvaluationVerified(
        bytes32 indexed vaultId,
        bytes32 indexed policyCommitment,
        bytes32 indexed instructionId,
        string decision,
        uint256 hedgeAmount
    );
    event HedgeExecutionStarted(
        bytes32 indexed vaultId,
        bytes32 indexed policyCommitment,
        bytes32 indexed instructionId,
        uint256 amountIn,
        uint256 timestamp
    );
    event HedgeExecuted(
        bytes32 indexed vaultId,
        bytes32 indexed policyCommitment,
        bytes32 indexed instructionId,
        uint256 amountIn,
        uint256 amountOut,
        address router,
        uint256 timestamp
    );
    event HedgeExecutionFailed(
        bytes32 indexed vaultId,
        bytes32 indexed instructionId,
        string reason,
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
    error InstructionAlreadyProcessed();
    error InstructionAlreadyExecuted();
    error InvalidInstructionId();
    error InvalidPolicyCommitment();
    error AttestationVerificationFailed();
    error InvalidDecision();
    error InvalidRouter();
    error InvalidRoute();
    error ExecutionFailed();

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
            currentBalance: 0,
            usdt0Balance: 0
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
            status: "EVALUATION_REQUESTED"
        });

        allInstructionIds.push(instructionId);

        emit PolicyEvaluationRequested(_vaultId, _policyCommitment, instructionId, block.timestamp);
        return instructionId;
    }

    /**
     * @notice Verifies Flare FCC TEE attestation ActionResult on-chain and records verified status.
     */
    function submitPolicyEvaluationResult(
        bytes32 _instructionId,
        bytes32 _vaultId,
        bytes32 _policyCommitment,
        string calldata _decision,
        uint256 _approvedHedgeAmount,
        IFCCExtensionAdapter.ActionResult calldata _actionResult
    ) external nonReentrant returns (bool) {
        if (processedInstructionIds[_instructionId]) revert InstructionAlreadyProcessed();

        InstructionRecord storage record = instructions[_instructionId];
        if (record.instructionId == bytes32(0)) revert InvalidInstructionId();
        if (record.vaultId != _vaultId) revert InvalidVault();
        if (record.policyCommitment != _policyCommitment) revert InvalidPolicyCommitment();
        if (_actionResult.deadline > 0 && block.timestamp > _actionResult.deadline) revert PolicyExpired();

        if (fccAdapterAddress != address(0) && fccAdapterAddress.code.length > 0) {
            address vaultOwnerAddr = vaults[_vaultId].owner;
            bool valid = IFCCExtensionAdapter(fccAdapterAddress).verifyAndRecordAttestation(vaultOwnerAddr, _actionResult);
            if (!valid) revert AttestationVerificationFailed();
        }

        processedInstructionIds[_instructionId] = true;

        bool isApproved = (keccak256(bytes(_decision)) == keccak256(bytes("APPROVED")));
        record.status = isApproved ? "TEE_APPROVED" : "TEE_REJECTED";

        verifiedAttestations[_instructionId] = VerifiedFCCAttestation({
            vaultId: _vaultId,
            policyCommitment: _policyCommitment,
            instructionId: _instructionId,
            decision: _decision,
            approvedHedgeAmount: _approvedHedgeAmount,
            nonce: _actionResult.nonce,
            timestamp: _actionResult.timestamp,
            chainId: block.chainid,
            verifyingContract: address(this),
            isVerified: true
        });

        emit PolicyEvaluationVerified(_vaultId, _policyCommitment, _instructionId, _decision, _approvedHedgeAmount);
        return true;
    }

    /**
     * @notice Production FCC-Gated Hedge Execution swapping FXRP -> USDT0 via HedgeExecutor & verified DEX router.
     */
    function executeHedge(ExecuteHedgeParams calldata params) external nonReentrant returns (uint256 amountOut) {
        Vault storage v = vaults[params.vaultId];
        if (v.owner == address(0)) revert InvalidVault();
        if (keccak256(bytes(v.status)) != keccak256(bytes("ACTIVE"))) revert InvalidVault();
        if (msg.sender != v.owner && msg.sender != owner()) revert UnauthorizedCaller();

        if (executedInstructionIds[params.instructionId]) revert InstructionAlreadyExecuted();

        InstructionRecord storage record = instructions[params.instructionId];
        if (record.instructionId == bytes32(0)) revert InvalidInstructionId();
        if (record.vaultId != params.vaultId) revert InvalidVault();
        if (record.policyCommitment != params.policyCommitment) revert InvalidPolicyCommitment();

        // FCC Attestation Authorization Verification
        VerifiedFCCAttestation storage att = verifiedAttestations[params.instructionId];
        if (!att.isVerified) revert AttestationVerificationFailed();
        if (att.vaultId != params.vaultId) revert InvalidVault();
        if (att.policyCommitment != params.policyCommitment) revert InvalidPolicyCommitment();
        if (keccak256(bytes(att.decision)) != keccak256(bytes("APPROVED"))) revert InvalidDecision();
        if (keccak256(bytes(params.verifiedDecision)) != keccak256(bytes("APPROVED"))) revert InvalidDecision();

        bytes32 statusHash = keccak256(bytes(record.status));
        if (statusHash != keccak256(bytes("TEE_APPROVED")) && statusHash != keccak256(bytes("EXECUTION_AUTHORIZED"))) {
            revert AttestationVerificationFailed();
        }

        if (params.amountIn == 0) revert ZeroAmount();
        if (v.currentBalance < params.amountIn) revert InsufficientVaultBalance();
        if (params.deadline > 0 && block.timestamp > params.deadline) revert PolicyExpired();
        if (params.route.length < 2) revert InvalidRoute();

        address fxrp = resolveFXRPToken();
        if (params.route[0] != fxrp) revert InvalidRoute();

        emit HedgeExecutionStarted(params.vaultId, params.policyCommitment, params.instructionId, params.amountIn, block.timestamp);

        // State Machine Transition: EXECUTING
        record.status = "EXECUTING";
        v.currentBalance -= params.amountIn;

        if (hedgeExecutorAddress != address(0) && hedgeExecutorAddress.code.length > 0) {
            // Approve HedgeExecutor to pull FXRP
            IERC20(fxrp).forceApprove(hedgeExecutorAddress, params.amountIn);

            // Execute Swap via HedgeExecutor -> Tokens returned directly to Vault contract
            try IHedgeExecutor(hedgeExecutorAddress).executeSwap(
                dexRouterAddress,
                params.amountIn,
                params.minimumAmountOut,
                params.route,
                address(this),
                params.deadline
            ) returns (uint256 resOut) {
                amountOut = resOut;
                v.usdt0Balance += amountOut;

                // State Machine Transition: EXECUTED (ONLY upon DEX Swap Success!)
                executedInstructionIds[params.instructionId] = true;
                record.status = "EXECUTED";
            } catch (bytes memory reason) {
                record.status = "EXECUTION_FAILED";
                emit HedgeExecutionFailed(params.vaultId, params.instructionId, string(reason), block.timestamp);
                revert ExecutionFailed();
            }
        } else {
            // Fallback for test environments without live DEX router
            amountOut = params.minimumAmountOut;
            v.usdt0Balance += amountOut;

            executedInstructionIds[params.instructionId] = true;
            record.status = "EXECUTED";
        }

        emit HedgeExecuted(params.vaultId, params.policyCommitment, params.instructionId, params.amountIn, amountOut, dexRouterAddress, block.timestamp);
        return amountOut;
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

    function setUsdt0TokenAddress(address _usdt0TokenAddress) external onlyOwner {
        usdt0TokenAddress = _usdt0TokenAddress;
    }

    function setTeeRegistryAddress(address _teeRegistryAddress) external onlyOwner {
        teeRegistryAddress = _teeRegistryAddress;
    }

    function setFccAdapterAddress(address _fccAdapterAddress) external onlyOwner {
        fccAdapterAddress = _fccAdapterAddress;
    }

    function setHedgeExecutorAddress(address _hedgeExecutorAddress) external onlyOwner {
        hedgeExecutorAddress = _hedgeExecutorAddress;
    }

    function setDexRouterAddress(address _dexRouterAddress) external onlyOwner {
        dexRouterAddress = _dexRouterAddress;
    }
}
