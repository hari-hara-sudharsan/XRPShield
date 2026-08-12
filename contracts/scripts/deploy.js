const { ethers } = require("hardhat");

async function main() {
  console.log("==========================================");
  console.log(" Deploying XRPShield Production Contracts ");
  console.log("==========================================");

  const [deployer] = await ethers.getSigners();
  console.log("Deployer Address:", deployer ? deployer.address : "Local Hardhat Signer");

  const fs = require("fs");
  const path = require("path");

  // 1. Deploy TestFXRPToken ERC-20
  const TestFXRPToken = await ethers.getContractFactory("TestFXRPToken");
  const fxrpToken = await TestFXRPToken.deploy();
  await fxrpToken.waitForDeployment();
  const fxrpAddress = await fxrpToken.getAddress();
  console.log("TestFXRPToken ERC-20 deployed to:", fxrpAddress);

  // 2. Deploy AccessManager
  const AccessManager = await ethers.getContractFactory("AccessManager");
  const accessManager = await AccessManager.deploy(deployer.address);
  await accessManager.waitForDeployment();
  const accessManagerAddress = await accessManager.getAddress();
  console.log("AccessManager deployed to:", accessManagerAddress);

  // 3. Deploy TreasuryStorage
  const TreasuryStorage = await ethers.getContractFactory("TreasuryStorage");
  const treasuryStorage = await TreasuryStorage.deploy();
  await treasuryStorage.waitForDeployment();
  const storageAddress = await treasuryStorage.getAddress();
  console.log("TreasuryStorage deployed to:", storageAddress);

  // 4. Deploy VaultManager
  const VaultManager = await ethers.getContractFactory("VaultManager");
  const vaultManager = await VaultManager.deploy(accessManagerAddress, storageAddress);
  await vaultManager.waitForDeployment();
  const vaultManagerAddress = await vaultManager.getAddress();
  console.log("VaultManager deployed to:", vaultManagerAddress);

  // 5. Authorize VaultManager in TreasuryStorage & Set FXRP ERC-20 token
  await treasuryStorage.setManagerContract(vaultManagerAddress);
  console.log("TreasuryStorage manager linked to VaultManager successfully.");

  await vaultManager.setFXRPToken(fxrpAddress);
  console.log("VaultManager linked to FXRP ERC-20 token contract successfully.");

  const deploymentData = {
    network: "coston2",
    chainId: 114,
    fxrpTokenAddress: fxrpAddress,
    accessManagerAddress: accessManagerAddress,
    treasuryStorageAddress: storageAddress,
    vaultManagerAddress: vaultManagerAddress,
    deployedAt: new Date().toISOString()
  };

  const manifestPath = path.join(__dirname, "../deployments.json");
  fs.writeFileSync(manifestPath, JSON.stringify(deploymentData, null, 2));
  console.log("Deployment manifest saved to:", manifestPath);

  console.log("\nDeployment Summary:");
  console.log("-------------------");
  console.log(`FXRP_TOKEN_ADDRESS=${fxrpAddress}`);
  console.log(`ACCESS_MANAGER_ADDRESS=${accessManagerAddress}`);
  console.log(`TREASURY_STORAGE_ADDRESS=${storageAddress}`);
  console.log(`VAULT_MANAGER_ADDRESS=${vaultManagerAddress}`);
}

main().catch((error) => {
  console.error("Deployment failed:", error);
  process.exitCode = 1;
});
