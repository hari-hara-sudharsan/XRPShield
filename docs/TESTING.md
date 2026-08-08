# XRPShield — Testing Strategy & Comprehensive Report

## 1. Overview
The XRPShield testing strategy combines automated Hardhat unit tests for Solidity smart contracts and JUnit 5 / Mockito unit & integration tests for the Spring Boot 3 Java service.

---

## 2. Test Execution & Coverage Summary

### Smart Contracts Test Suite (`contracts/test`)
- **Framework:** Hardhat / Mocha / Chai / Ethers.js v6.
- **Coverage:** 100% contract compilation and initialization verification.
- **Passing Tests:**
  - `VaultManager.test.js`: Contract deployment, AccessManager link, Vault registration, event emissions (`VaultRegistered`, `VaultUpdated`), pause/unpause safety enforcement.
  - `XRPShieldHealth.test.js`: System status verification.

### Backend JUnit 5 Test Suite (`backend/src/test/java/com/xrpshield`)
- **Framework:** JUnit 5, Spring Boot Test, Mockito.
- **Passing Tests:**
  - `Web3SignatureVerifierTest`: Signature recovery format & null safety verification.
  - `JwtTokenProviderTest`: Access token generation, claims extraction, expiration validation.
  - `AuthServiceTest`: User registration, BCrypt password hashing, auth token issuance.
  - `WalletAuthServiceTest`: Challenge nonce generation & wallet linkage.
  - `SystemControllerTest`: Aggregated status endpoint response checks.
  - `BlockchainControllerTest`: RPC status and latest block metadata API verification.

---

## 3. How to Run Tests Locally

### Run Hardhat Contract Tests
```bash
cd contracts
npx hardhat test
```

### Run Spring Boot Unit & Integration Tests
```bash
cd backend
mvn clean test
```

### Run Unified Build Script
```powershell
.\scripts\build.ps1
```
