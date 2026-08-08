# XRPShield — Comprehensive REST API Reference Documentation

## 1. Authentication & Security Endpoints (`/api/v1/auth`, `/api/v1/wallet`)

### `POST /api/v1/auth/register`
- **Description:** Registers a new organization user account.
- **Request Body:** `{ "email": "user@domain.com", "fullName": "Alice Smith", "password": "SecretPassword123!" }`
- **Response (201):** `{ "success": true, "message": "User registered successfully", "data": { "id": "uuid", "email": "user@domain.com", "role": "ROLE_USER" } }`

### `POST /api/v1/auth/login`
- **Description:** Authenticates user and returns JWT Bearer token.
- **Request Body:** `{ "email": "user@domain.com", "password": "SecretPassword123!" }`
- **Response (200):** `{ "success": true, "data": { "token": "eyJhbGciOi...", "type": "Bearer", "email": "user@domain.com" } }`

### `POST /api/v1/wallet/nonce`
- **Description:** Generates Web3 authentication nonce for wallet address.
- **Request Body:** `{ "walletAddress": "0x5FbDB2315678afecb367f032d93F642f64180aa3" }`

- **Response (200):** `{ "success": true, "data": { "nonce": "XRPShield Auth Nonce: uuid" } }`

---

## 2. Vault Management Endpoints (`/api/v1/vault`)

### `POST /api/v1/vault`
- **Description:** Registers a new confidential FXRP vault on-chain.
- **Request Body:** `{ "vaultName": "Primary FXRP Treasury", "vaultAddress": "0x123...", "assetType": "FXRP" }`
- **Response (201):** `{ "success": true, "data": { "id": "uuid", "vaultName": "Primary FXRP Treasury", "status": "ACTIVE" } }`

### `GET /api/v1/vault`
- **Description:** Lists all vaults owned by the authenticated user.
- **Response (200):** List of `VaultResponseDto` objects.

---

## 3. Confidential Policy Endpoints (`/api/v1/policies`)

### `POST /api/v1/policies`
- **Description:** Creates confidential policy, encrypts payload (AES-256-GCM), and logs TEE attestation proof.
- **Request Body:** `{ "vaultId": "uuid", "policyName": "Treasury Risk Guard", "maxDrawdownPercent": 10.0, "minLiquidityThreshold": 50000.0 }`
- **Response (201):** `{ "success": true, "data": { "id": "uuid", "attestationId": "FCC-ATT-FD1A77E2", "policyHash": "0x51ed..." } }`

---

## 4. Decision Engine Endpoints (`/api/v1/decision`)

### `POST /api/v1/decision/evaluate`
- **Description:** Evaluates confidential policy inside Flare TEE enclaves and generates versioned decision.
- **Request Body:** `{ "vaultId": "uuid", "preferredDecisionType": "PROTECT_POSITION" }`
- **Response (201):** `{ "success": true, "data": { "id": "uuid", "decisionType": "PROTECT_POSITION", "status": "PENDING", "version": 1 } }`

---

## 5. Protected Execution Endpoints (`/api/v1/execution`)

### `POST /api/v1/execution/start`
- **Description:** Submits approved decision to protected on-chain execution engine.
- **Request Body:** `{ "decisionId": "uuid" }`
- **Response (201):** `{ "success": true, "data": { "id": "uuid", "executionState": "COMPLETED", "txHash": "0x7f82..." } }`

---

## 6. AI Intelligence Endpoints (`/api/v1/ai`)

### `POST /api/v1/ai/policy`
- **Description:** Generates draft policy JSON from natural language intent with privacy filter guard.
- **Request Body:** `{ "intent": "Protect vault when drawdown > 10%" }`
- **Response (201):** Structured policy draft JSON.

---

## 7. Platform Status Endpoints (`/api/v1/platform`)

### `GET /api/v1/platform/status`
- **Description:** Returns operational status across all 6 platform sub-components.
- **Response (200):** `{ "overallStatus": "HEALTHY", "components": [...] }`
