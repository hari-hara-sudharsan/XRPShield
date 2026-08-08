# XRPShield — Production Deployment Checklist

## 1. Pre-Deployment Verification Checklist
- [x] All 13 Solidity Smart Contracts compiled and verified via Hardhat unit tests (`npx hardhat test`).
- [x] Spring Boot 3 Java backend compiles with zero errors (`mvn clean compile test`).
- [x] Database migrations V1, V2, V3, V4, V5 tested against Supabase PostgreSQL schema.
- [x] Security headers (`SecurityHeadersFilter`) and rate limiting (`RateLimiterFilter`) enabled.
- [x] GitHub Actions CI/CD matrix pipeline configured (`.github/workflows/ci.yml`).

---

## 2. Environment Variables Configuration

| Variable Name | Production Setting | Description |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | `prod` | Active Spring profile |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<SUPABASE_HOST>:5432/postgres` | PostgreSQL DB URL |
| `SPRING_DATASOURCE_USERNAME` | `<DB_USER>` | Supabase Database Username |
| `SPRING_DATASOURCE_PASSWORD` | `<DB_PASSWORD>` | Supabase Database Password |
| `JWT_SECRET` | `<SECURE_256_BIT_KEY>` | JWT Secret Signature Key |
| `FLARE_RPC_URL` | `https://coston2-api.flare.network/ext/C/rpc` | Flare Coston2 Testnet RPC |
| `FLARE_CHAIN_ID` | `114` | Flare C-Chain Network ID |
