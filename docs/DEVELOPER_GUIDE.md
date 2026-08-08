# XRPShield — Developer & Setup Guide

## 1. Prerequisites
- **Java 21 LTS** & **Maven 3.9+**
- **Node.js v18+** & **npm**
- **Hardhat** (Solidity 0.8.24 compiler)
- **Git**

---

## 2. Local Environment Setup

### A. Clone Repository & Setup Environment
```bash
git clone https://github.com/xrpshield/xrpshield.git
cd xrpshield
cp .env.example .env
```

### B. Compile Smart Contracts
```bash
cd contracts
npm install
npx hardhat compile
npx hardhat test
```

### C. Build & Run Spring Boot Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### D. Automated Full Build Script
```powershell
.\scripts\build.ps1
```
