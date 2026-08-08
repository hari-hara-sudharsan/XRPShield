# XRPShield — Developer Local Setup Guide

## Quickstart for Local Development

### 1. Prerequisites Check
Ensure Java 21+, Maven 3.9+, Node.js 20+, and npm 10+ are installed.

```bash
java -version
mvn -version
node -v
npm -v
```

---

### 2. Clone Repository & Environment Setup
```bash
git clone https://github.com/xrpshield/xrpshield.git
cd XRPShield
cp .env.example .env
```

---

### 3. Build & Test Smart Contracts
```bash
cd contracts
npm install
npx hardhat test
```

---

### 4. Build & Test Spring Boot Backend
```bash
cd backend
mvn clean compile test
mvn spring-boot:run
```

---

### 5. Launch Frontend UI
Open `frontend/index.html` directly in a browser or serve using local web server (e.g. `npx http-server ./frontend -p 3000`).

---

### 6. Verify Health Endpoints
- **Health Check REST API:** `http://localhost:8080/api/v1/health`
- **Spring Actuator Endpoint:** `http://localhost:8080/actuator/health`
- **Swagger OpenApi Documentation:** `http://localhost:8080/swagger-ui.html`
