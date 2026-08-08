# XRPShield — Production Smart Contract Architecture

## 1. Overview
The XRPShield smart contract suite runs on the **Flare Network** (Solidity v0.8.24). It provides decentralized access control, storage decoupling, and vault lifecycle tracking for confidential XRP/FXRP treasury reserves.

---

## 2. Core Contracts Architecture

```
                                +-------------------+
                                |   AccessManager   |
                                |  (RBAC & Roles)   |
                                +---------+---------+
                                          |
                                          v
+--------------------+          +---------+---------+
|   TreasuryStorage  | <------- |   VaultManager    |
| (State Decoupled)  |          | (Registry Core)   |
+--------------------+          +-------------------+
```

### Contract Deliverables
1. **`VaultManager.sol`:** Production vault registry contract inheriting OpenZeppelin `ReentrancyGuard` and `Pausable`. Manages vault registration, status updates (`ACTIVE`, `PAUSED`, `CLOSED`), and emits `VaultRegistered` and `VaultUpdated` events.
2. **`AccessManager.sol`:** Role-based access control contract defining `DEFAULT_ADMIN_ROLE`, `OPERATOR_ROLE`, and `PAUSER_ROLE`.
3. **`TreasuryStorage.sol`:** Decoupled storage layout contract insulating persistent state variables from logic upgrades.
4. **`CommonErrors.sol`:** Gas-optimized custom errors (`UnauthorizedCaller`, `VaultAlreadyRegistered`, `VaultNotFound`, `SystemPaused`, `ZeroAddressDetected`).
5. **`IVaultManager.sol`:** Solidity interface contract.

---

## 3. Events Reference
```solidity
event VaultRegistered(address indexed vaultAddress, address indexed owner, string name, uint256 timestamp);
event VaultUpdated(address indexed vaultAddress, VaultStatus newStatus, uint256 timestamp);
event Paused(address account);
event Unpaused(address account);
```
