# XRPShield — Technology Stack Specifications

| Component | Framework / Tool | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language Runtime** | Java | 21 LTS | High-performance backend execution environment |
| **Backend Framework** | Spring Boot | 3.2.3 | Production web framework, Actuator monitoring, REST services |
| **Security** | Spring Security + JJWT | 0.12.5 | Stateless JWT authentication & EIP-4361 wallet verification |
| **Database** | PostgreSQL (Supabase) | 15+ | Relational data store for users, policies, audit logs |
| **Build Tool** | Apache Maven | 3.9+ | Backend dependency management & lifecycle builder |
| **Smart Contracts** | Solidity | 0.8.24 | Flare network smart contract execution |
| **Contract Framework** | Hardhat | 2.22+ | Compilation, testing, deployment, Ethers v6 binding |
| **Blockchain Network** | Flare Coston2 / Mainnet | Chain ID 114 | Smart contract deployment target & FTSO oracles |
| **Frontend Framework** | HTML5 / CSS3 / ES6 JS | Native Standard | Zero-dependency, fast, lightweight responsive client shell |
