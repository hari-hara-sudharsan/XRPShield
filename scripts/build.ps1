# Powershell Build Script for XRPShield
$ErrorActionPreference = "Stop"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host " Building XRPShield Production Foundation " -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

$root = Resolve-Path "$PSScriptRoot\.."

Write-Host "`n[1/2] Compiling Smart Contracts (Hardhat)..." -ForegroundColor Yellow
Set-Location "$root\contracts"
if (Test-Path "node_modules") {
    npx hardhat compile
} else {
    Write-Host "Installing contract dependencies..." -ForegroundColor Gray
    npm install --silent
    npx hardhat compile
}

Write-Host "`n[2/2] Building Backend (Spring Boot / Maven)..." -ForegroundColor Yellow
Set-Location "$root\backend"
$mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvnCmd) {
    mvn clean compile
} else {
    & "C:\Program Files\JetBrains\IntelliJ IDEA 2024.3.2.2\plugins\maven\lib\maven3\bin\mvn.cmd" clean compile
}

Set-Location $root
Write-Host "`n==========================================" -ForegroundColor Green
Write-Host " XRPShield Build Completed Successfully!  " -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
