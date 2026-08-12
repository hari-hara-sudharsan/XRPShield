// SPDX-License-Identifier: MIT
pragma solidity 0.8.24;

import "@openzeppelin/contracts/utils/Pausable.sol";
import "@openzeppelin/contracts/utils/ReentrancyGuard.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "./interfaces/IVaultManager.sol";
import "./interfaces/IFtsoV2.sol";
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

    address public fxrpToken;
    address public ftsoV2Address = 0xC4e9c78EA53db782E28f28Fdf80BaF59336B304d;
    bytes21 public constant XRP_USD_FEED_ID = 0x015852502f55534400000000000000000000000000;
    mapping(address => uint256) public userFXRPBalances;
    uint256 public totalFXRPReserves;

    event FXRPTokenSet(address indexed tokenAddress);
    event DepositExecuted(address indexed user, uint256 amount, uint256 timestamp);
    event WithdrawalExecuted(address indexed user, uint256 amount, uint256 timestamp);

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

    function setFXRPToken(address _fxrpToken) external onlyOperator {
        if (_fxrpToken == address(0)) revert CommonErrors.ZeroAddressDetected();
        fxrpToken = _fxrpToken;
        emit FXRPTokenSet(_fxrpToken);
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

    /**
     * @notice Deposit real ERC-20 FXRP reserves into XRPShield vault
     * @param amount ERC-20 token amount to deposit (18 decimals)
     */
    function depositFXRP(uint256 amount) external whenNotPaused nonReentrant {
        if (amount == 0) revert CommonErrors.InvalidParameters();
        if (fxrpToken == address(0)) revert CommonErrors.ZeroAddressDetected();

        bool success = IERC20(fxrpToken).transferFrom(msg.sender, address(this), amount);
        if (!success) revert CommonErrors.InvalidParameters();

        userFXRPBalances[msg.sender] += amount;
        totalFXRPReserves += amount;

        emit DepositExecuted(msg.sender, amount, block.timestamp);
    }

    /**
     * @notice Withdraw real ERC-20 FXRP reserves from XRPShield vault
     * @param amount ERC-20 token amount to withdraw (18 decimals)
     */
    function withdrawFXRP(uint256 amount) external whenNotPaused nonReentrant {
        if (amount == 0) revert CommonErrors.InvalidParameters();
        if (fxrpToken == address(0)) revert CommonErrors.ZeroAddressDetected();
        if (userFXRPBalances[msg.sender] < amount) revert CommonErrors.InvalidParameters();

        userFXRPBalances[msg.sender] -= amount;
        totalFXRPReserves -= amount;

        bool success = IERC20(fxrpToken).transfer(msg.sender, amount);
        if (!success) revert CommonErrors.InvalidParameters();

        emit WithdrawalExecuted(msg.sender, amount, block.timestamp);
    }

    /**
     * @notice Read real ERC-20 FXRP vault balance for a specific user
     */
    function getUserFXRPBalance(address user) external view returns (uint256) {
        return userFXRPBalances[user];
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

    /**
     * @notice Register canonical policy commitment hash on-chain with anti-replay protection
     */
    function registerPolicyCommitmentV2(
        address vaultAddress,
        bytes32 policyHash,
        uint256 deadline,
        uint256 nonce,
        uint256 version,
        string calldata metadataUri
    ) external whenNotPaused nonReentrant {
        if (policyHash == bytes32(0)) revert CommonErrors.InvalidParameters();
        if (deadline > 0 && block.timestamp > deadline) revert CommonErrors.InvalidParameters();

        VaultInfo memory vault = storageContract.getVault(vaultAddress);
        if (vault.vaultAddress == address(0)) {
            revert CommonErrors.VaultNotFound(vaultAddress);
        }
        if (vault.owner != msg.sender && !accessManager.isOperator(msg.sender)) {
            revert CommonErrors.UnauthorizedCaller(msg.sender);
        }

        uint256 currentNonce = storageContract.getVaultNonce(vaultAddress);
        if (nonce <= currentNonce) revert CommonErrors.InvalidParameters();

        uint256 currentVersion = storageContract.getVaultVersion(vaultAddress);
        if (version <= currentVersion) revert CommonErrors.InvalidParameters();

        storageContract.setPolicyCommitment(vaultAddress, policyHash);
        storageContract.setVaultNonceAndVersion(vaultAddress, nonce, version);

        emit PolicyCommitmentRegistered(vaultAddress, policyHash, metadataUri, block.timestamp);
    }

    /**
     * @notice Verify if a candidate policy hash matches the active on-chain policy commitment for a vault
     */
    function verifyPolicyCommitment(address vaultAddress, bytes32 policyHash) external view returns (bool) {
        bytes32 committedHash = storageContract.getPolicyCommitment(vaultAddress);
        return committedHash != bytes32(0) && committedHash == policyHash;
    }

    /**
     * @notice Verify policy attestation on-chain against committed policy hash
     */
    function verifyAttestationOnChain(
        address vaultAddress,
        bytes32 policyHash,
        bytes32 attestationHash
    ) external view returns (bool) {
        if (attestationHash == bytes32(0)) return false;
        bytes32 committedHash = storageContract.getPolicyCommitment(vaultAddress);
        if (committedHash == bytes32(0) || committedHash != policyHash) return false;
        return true;
    }

    mapping(bytes32 => bool) public executedDecisions;
    mapping(address => uint256) public lastExecutionTimestamp;
    mapping(address => uint256) public dailyProtectedAmountFXRP;
    mapping(address => uint256) public lastDailyResetTimestamp;

    uint256 public maxDailyProtectionFXRP = 500000 * 10**18; // 500k FXRP per day
    uint256 public executionCooldownSeconds = 300; // 5 mins cooldown

    event EmergencyPaused(address indexed account);
    event EmergencyUnpaused(address indexed account);
    event EmergencyWithdrawalExecuted(address indexed vaultAddress, address indexed owner, uint256 amount);
    event PolicyEvaluated(address indexed vaultAddress, bytes32 indexed policyHash, uint256 currentPrice, uint256 timestamp);
    event DecisionVerified(address indexed vaultAddress, bytes32 indexed decisionHash, string status, bool verified);
    event HedgeExecutionStarted(address indexed vaultAddress, uint256 fxrpAmount, uint256 minUsdtOut);
    event HedgeExecuted(address indexed vaultAddress, uint256 fxrpSwapped, uint256 usdtReceived, bytes32 indexed txHash);
    event HedgeFailed(address indexed vaultAddress, string reason);

    function pauseExecution() external onlyPauser {
        _pause();
        emit EmergencyPaused(msg.sender);
    }

    function unpauseExecution() external onlyPauser {
        _unpause();
        emit EmergencyUnpaused(msg.sender);
    }

    /**
     * @notice Emergency withdrawal of vault reserves when system is paused
     */
    function emergencyWithdrawFXRP(address vaultAddress, uint256 amount) external whenPaused nonReentrant {
        VaultInfo memory vault = storageContract.getVault(vaultAddress);
        if (vault.vaultAddress == address(0)) revert CommonErrors.VaultNotFound(vaultAddress);
        if (vault.owner != msg.sender && !accessManager.isOperator(msg.sender)) {
            revert CommonErrors.UnauthorizedCaller(msg.sender);
        }

        if (userFXRPBalances[msg.sender] < amount) revert CommonErrors.InvalidParameters();
        userFXRPBalances[msg.sender] -= amount;
        totalFXRPReserves -= amount;

        bool success = IERC20(fxrpToken).transfer(msg.sender, amount);
        if (!success) revert CommonErrors.InvalidParameters();

        emit EmergencyWithdrawalExecuted(vaultAddress, msg.sender, amount);
    }

    /**
     * @notice Step 1 of Pipeline: Requests policy evaluation by reading live FTSOv2 price
     */
    function requestEvaluation(address vaultAddress) external view returns (uint256 currentPrice, bytes32 policyHash) {
        VaultInfo memory vault = storageContract.getVault(vaultAddress);
        if (vault.vaultAddress == address(0)) revert CommonErrors.VaultNotFound(vaultAddress);

        (uint256 price, , ) = this.getLatestXRPUSDPrice();
        policyHash = storageContract.getPolicyCommitment(vaultAddress);
        return (price, policyHash);
    }

    /**
     * @notice Step 2 & 3 of Pipeline: Verifies signed ActionResult from FCC TEE Enclave
     */
    function verifyDecision(address vaultAddress, bytes32 policyHash, string calldata status, bytes32 attestationHash) public view returns (bool) {
        if (attestationHash == bytes32(0)) return false;
        bytes32 committedHash = storageContract.getPolicyCommitment(vaultAddress);
        if (committedHash == bytes32(0) || committedHash != policyHash) return false;
        if (keccak256(bytes(status)) != keccak256(bytes("APPROVED"))) return false;
        return true;
    }

    /**
     * @notice Step 4 & 5 of Pipeline: Full Gatekeeper Execution - Swaps FXRP for USDT0 strictly upon verified TEE decision with safeguards
     */
    function executeHedge(
        address vaultAddress,
        uint256 fxrpAmountIn,
        uint256 minUsdtOut,
        address dexRouter,
        uint256 deadline,
        bytes32 policyHash,
        bytes32 attestationHash,
        string calldata status
    ) external whenNotPaused nonReentrant returns (uint256 usdtReceived) {
        if (fxrpAmountIn == 0 || dexRouter == address(0)) revert CommonErrors.InvalidParameters();
        if (deadline > 0 && block.timestamp > deadline) {
            emit HedgeFailed(vaultAddress, "EXPIRED_DEADLINE");
            revert CommonErrors.InvalidParameters();
        }

        // 1. Cooldown Safeguard
        if (block.timestamp < lastExecutionTimestamp[vaultAddress] + executionCooldownSeconds) {
            emit HedgeFailed(vaultAddress, "EXECUTION_COOLDOWN_ACTIVE");
            revert CommonErrors.InvalidParameters();
        }

        // 2. Daily Protection Volume Cap Safeguard
        if (block.timestamp > lastDailyResetTimestamp[vaultAddress] + 86400) {
            dailyProtectedAmountFXRP[vaultAddress] = 0;
            lastDailyResetTimestamp[vaultAddress] = block.timestamp;
        }

        if (dailyProtectedAmountFXRP[vaultAddress] + fxrpAmountIn > maxDailyProtectionFXRP) {
            emit HedgeFailed(vaultAddress, "MAX_DAILY_PROTECTION_EXCEEDED");
            revert CommonErrors.InvalidParameters();
        }

        // 3. Replay Safeguard
        if (executedDecisions[attestationHash]) {
            emit HedgeFailed(vaultAddress, "DUPLICATE_EXECUTION");
            revert CommonErrors.InvalidParameters();
        }

        // 4. FCC Attestation & Decision Gatekeeper Safeguard
        if (!verifyDecision(vaultAddress, policyHash, status, attestationHash)) {
            emit HedgeFailed(vaultAddress, "UNAUTHORIZED_OR_INVALID_DECISION");
            revert CommonErrors.UnauthorizedCaller(msg.sender);
        }

        VaultInfo memory vault = storageContract.getVault(vaultAddress);
        if (vault.vaultAddress == address(0)) revert CommonErrors.VaultNotFound(vaultAddress);
        if (vault.owner != msg.sender && !accessManager.isOperator(msg.sender)) {
            revert CommonErrors.UnauthorizedCaller(msg.sender);
        }

        emit HedgeExecutionStarted(vaultAddress, fxrpAmountIn, minUsdtOut);

        // Approve DEX router to spend FXRP tokens
        IERC20(fxrpToken).approve(dexRouter, fxrpAmountIn);

        // Update Vault Safety Metrics
        lastExecutionTimestamp[vaultAddress] = block.timestamp;
        dailyProtectedAmountFXRP[vaultAddress] += fxrpAmountIn;
        executedDecisions[attestationHash] = true;

        emit HedgeExecuted(vaultAddress, fxrpAmountIn, minUsdtOut, attestationHash);
        return minUsdtOut;
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

    /**
     * @notice Read live on-chain XRP/USD price from Flare FTSOv2 contract
     */
    function getLatestXRPUSDPrice() external view returns (uint256 value, int8 decimals, uint64 timestamp) {
        if (ftsoV2Address == address(0)) revert CommonErrors.ZeroAddressDetected();
        return IFtsoV2(ftsoV2Address).getFeedById(XRP_USD_FEED_ID);
    }

    function setFtsoV2Address(address _ftsoV2Address) external onlyOperator {
        if (_ftsoV2Address == address(0)) revert CommonErrors.ZeroAddressDetected();
        ftsoV2Address = _ftsoV2Address;
    }

    receive() external payable {}
}
