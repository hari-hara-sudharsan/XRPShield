# XRPShield — Backend Web3j Blockchain Infrastructure

## 1. Web3j Integration Architecture
The Spring Boot backend interfaces with the **Flare Network** via standard JSON-RPC powered by **Web3j 5.0.0**.

---

## 2. Key Components (`com.xrpshield.blockchain`)
- **`BlockchainClient.java`:** Core Web3j wrapper providing connection pooling, block number fetching (`getLatestBlockNumber`), gas price retrieval (`getGasPrice`), and RPC health checks (`isConnected`).
- **`ContractService.java`:** Handles deployed contract metadata, ABI registration, and contract address lookups.
- **`EventListener.java`:** Monitored event listener capturing on-chain events (`VaultRegistered`, `VaultUpdated`, `Paused`, `Unpaused`) and persisting log records into `blockchain_event_logs`.
- **`GasEstimator.java`:** Real-time gas price evaluation service.
- **`NetworkService.java`:** Network parameter aggregation and status monitoring.

---

## 3. Blockchain REST API Reference

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/api/v1/blockchain/status` | `GET` | Network status, Chain ID, RPC connection, block height |
| `/api/v1/blockchain/network` | `GET` | Flare Coston2 configuration & explorer URLs |
| `/api/v1/blockchain/contracts` | `GET` | Deployed contract metadata registry |
| `/api/v1/blockchain/health` | `GET` | Blockchain RPC health check |
| `/api/v1/blockchain/latest-block` | `GET` | Latest block height and current gas price |
