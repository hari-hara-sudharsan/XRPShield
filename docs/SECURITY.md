# XRPShield — Security Architecture & Threat Model

## 1. Password Security
- **Algorithm:** BCrypt password hashing (`BCryptPasswordEncoder`) with work factor 10.
- **Plaintext Policy:** Plaintext passwords are never logged, cached, or stored in database columns.
- **Strength Enforcements:** Minimum 8 characters up to 64 characters enforced via Bean Validation (`@Size`).

---

## 2. JWT Access & Refresh Token Architecture
- **Signing Algorithm:** HMAC SHA-256 (`HS256`) using a 256-bit secret key (`JWT_SECRET`).
- **Access Token Expiration:** 24 hours (86,400,000 ms).
- **Refresh Token Expiration:** 7 days. Stored in `refresh_tokens` database table with revocation flags (`revoked`).
- **Token Claims:** `sub` (User UUID), `identifier` (Email or Wallet Address), `role` (`ROLE_USER` or `ROLE_ADMIN`).

---

## 3. Cryptographic Wallet Authentication (EIP-191 / EIP-4361)
- **Nonce Generation:** Secure random UUID nonces generated server-side per wallet authentication attempt to prevent replay attacks.
- **Signature Recovery:** Web3j ECRecover computes public key from prefix `\x19Ethereum Signed Message:\n<length><message>` and extracts public address.

---

## 4. Role-Based Access Control (RBAC)
- `ROLE_USER`: Standard authorization level for managing personal vaults and policies.
- `ROLE_ADMIN`: Administrative authorization level for platform monitoring and global settings.
- Enforced using `@EnableMethodSecurity` and Spring Security URL pattern matchers.
