# XRPShield — Production Deployment Guide

## 1. Prerequisites
- Docker Engine 24+ & Docker Compose (optional for containerization)
- JDK 21 installed on target deployment host
- Node.js 20+ installed for Hardhat deployment scripts
- PostgreSQL 15+ database (Supabase instance)

---

## 2. Environment Configuration
1. Copy `.env.example` to `.env` on your deployment host.
2. Fill in production database parameters (`DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`).
3. Set your production `JWT_SECRET` (at least 256 bits).
4. Set Flare Network RPC (`FLARE_RPC_URL`) and deployer `PRIVATE_KEY`.

---

## 3. Building & Deploying Smart Contracts
```bash
cd contracts
npm install
npx hardhat run scripts/deploy.js --network coston2
```

---

## 4. Building & Running Backend Service
```bash
cd backend
mvn clean package -DskipTests
java -jar target/xrpshield-backend-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## 5. Serving Frontend Application
The `/frontend` folder contains static HTML5/CSS3/ES6 assets. Serve using Nginx, Apache, or AWS CloudFront/S3:
```nginx
server {
    listen 80;
    server_name app.xrpshield.io;
    root /var/www/xrpshield/frontend;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```
