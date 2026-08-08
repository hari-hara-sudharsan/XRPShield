# XRPShield — Performance Benchmark & Optimization Strategy

## 1. Database Connection & Query Tuning
- **Connection Pool:** HikariCP connection pool configured for optimal throughput (`maximum-pool-size: 10`, `minimum-idle: 5`, `idle-timeout: 300000`).
- **Database Indexing:** Indexed foreign keys and high-frequency lookup fields (`contract_address`, `tx_hash`, `flag_key`, `email`, `wallet_address`).

---

## 2. Web3 RPC Latency & Async Performance
- **Asynchronous Sync Schedulers:** Background block polling runs on dedicated Spring `@Scheduled` thread pools to avoid blocking client requests.
- **Web3j HTTP Service:** Reusable connection pool to minimize TCP handshake overhead on Flare RPC endpoints (`https://coston2-api.flare.network`).
