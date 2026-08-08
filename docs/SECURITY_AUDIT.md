# XRPShield — Security Hardening & Threat Model Audit

## 1. Security Architecture Summary
XRPShield follows defense-in-depth security principles across smart contracts, authentication, database storage, and API transport layers.

---

## 2. OWASP Top 10 Security Controls & Mitigation Matrix

| Vulnerability Category | Risk Mitigation Implementation |
| :--- | :--- |
| **A01: Broken Access Control** | Enforced via Spring Security `SecurityConfig` URL matchers and `@EnableMethodSecurity` RBAC (`ROLE_USER`, `ROLE_ADMIN`). |
| **A02: Cryptographic Failures** | BCrypt password hashing ($2a$10 strength), HMAC-SHA256 JWT signing, and Web3j ECRecover EIP-191 signature verification. |
| **A03: Injection (SQLi/XSS)** | Enforced via Spring Data JPA parameterized queries and strict input validation (`@Email`, `@NotBlank`, `@Size`). |
| **A04: Insecure Design** | Rate limiting filter (`RateLimiterFilter`) protecting `/api/v1/auth/login` and `/api/v1/wallet/verify` against brute-force attacks. |
| **A05: Security Misconfiguration**| Custom `SecurityHeadersFilter` injecting OWASP recommended headers (`X-Content-Type-Options`, `X-Frame-Options`, `X-XSS-Protection`, `Strict-Transport-Security`, `Content-Security-Policy`). |

---

## 3. Web3 Cryptographic Nonce Security
- Server-generated UUID nonces expire after 10 minutes.
- Nonce invalidation prevents signature replay attacks across chains or sessions.
