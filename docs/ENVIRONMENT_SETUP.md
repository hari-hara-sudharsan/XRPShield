# XRPShield — Environment Setup Guide

## Environment Variables Reference

| Variable Name | Required | Default | Description |
| :--- | :--- | :--- | :--- |
| `APP_PORT` | No | `8080` | Port for Spring Boot HTTP REST Server |
| `SPRING_PROFILES_ACTIVE` | No | `dev` | Active Spring profile (`dev` or `prod`) |
| `DATABASE_URL` | Yes | Local fallback | JDBC PostgreSQL connection string |
| `DATABASE_USERNAME` | Yes | `postgres` | Database connection username |
| `DATABASE_PASSWORD` | Yes | `postgres` | Database connection password |
| `SUPABASE_URL` | Yes | None | Supabase API endpoint |
| `SUPABASE_KEY` | Yes | None | Supabase anon key |
| `FLARE_RPC_URL` | No | `https://coston2-api.flare.network/ext/C/rpc` | Flare RPC endpoint |
| `CHAIN_ID` | No | `114` | Target EVM Chain ID |
| `PRIVATE_KEY` | Yes (for deployment) | None | Deployment EVM wallet private key |
| `JWT_SECRET` | Yes | Default string | Secret key for signing JWT tokens |
| `JWT_EXPIRATION_MS` | No | `86400000` | JWT validity duration (1 day) |
| `OPENAI_API_KEY` | No | `disabled` | OpenAI API key for assistant features |
