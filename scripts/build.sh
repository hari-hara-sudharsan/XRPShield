#!/usr/bin/env bash
set -e

echo "=========================================="
echo " Building XRPShield Production Foundation "
echo "=========================================="

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

echo -e "\n[1/2] Compiling Smart Contracts (Hardhat)..."
cd "$ROOT_DIR/contracts"
if [ ! -d "node_modules" ]; then
    npm install
fi
npx hardhat compile

echo -e "\n[2/2] Building Backend (Spring Boot / Maven)..."
cd "$ROOT_DIR/backend"
mvn clean compile

echo -e "\n=========================================="
echo " XRPShield Build Completed Successfully!  "
echo "=========================================="
