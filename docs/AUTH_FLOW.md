# XRPShield — Authentication & Identity Architecture

## 1. Overview
XRPShield supports dual production authentication methods:
1. **Web3 Cryptographic Signature Authentication (EIP-191 / EIP-4361):** Users sign a server-generated challenge nonce using their MetaMask EVM wallet (`personal_sign`). The backend recovers the public address using Web3j ECRecover and issues a signed JWT token pair.
2. **Password Authentication:** Standard email/password registration with BCrypt password hashing ($2a$10 strength) and role-based authorization.

---

## 2. Web3 MetaMask Wallet Signature Flow Diagram

```mermaid
sequenceDiagram
    autonumber
    actor User as User (MetaMask Wallet)
    participant Client as Frontend SPA (Vanilla JS)
    participant API as Backend (Spring Boot 3)
    participant Verifier as Web3 Signature Verifier
    participant DB as Supabase PostgreSQL

    User->>Client: Click "Connect MetaMask"
    Client->>User: Request window.ethereum account access
    User-->>Client: Returns EVM Wallet Address (0x...)
    
    Client->>API: POST /api/v1/wallet/nonce { address: "0x..." }
    API->>DB: Store Session & Generated Nonce
    API-->>Client: Return Challenge Nonce & Message to Sign
    
    Client->>User: Prompt personal_sign(Message, Address)
    User-->>Client: Return Cryptographic Signature (0x...)
    
    Client->>API: POST /api/v1/wallet/verify { address, signature, nonce }
    API->>Verifier: verifySignature(address, message, signature)
    Verifier->>Verifier: ECRecover public key & compare address
    
    alt Signature Valid
        Verifier-->>API: Verified (True)
        API->>DB: Record Login History & Persist Wallet Link
        API->>API: Issue JWT Access Token & Refresh Token
        API-->>Client: Return 200 OK + AuthResponseDto (JWT)
        Client->>User: Save Token & Grant Access
    else Signature Invalid
        Verifier-->>API: Verification Failed (False)
        API-->>Client: Return 422 Unprocessable Entity (Invalid Signature)
    end
```

---

## 3. Password Authentication Flow Diagram

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant Client as Frontend SPA
    participant Controller as AuthController
    participant Service as AuthService
    participant Encoder as BCryptPasswordEncoder
    participant DB as Supabase PostgreSQL

    User->>Client: Submit Registration / Login Form
    Client->>Controller: POST /api/v1/auth/login { email, password }
    Controller->>Service: login(LoginRequestDto)
    Service->>DB: Find User by Email
    Service->>Encoder: matches(rawPassword, encodedPassword)
    
    alt Credentials Match
        Service->>DB: Save RefreshToken & Record Login History
        Service-->>Controller: Return AuthResponseDto (JWT)
        Controller-->>Client: Return 200 OK + JWT
    else Invalid Password
        Service->>DB: Record Failed Login Attempt
        Service-->>Controller: Throw BusinessException
        Controller-->>Client: Return 422 Unprocessable Entity
    end
```
