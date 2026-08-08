const { ethers } = require("hardhat");

async function main() {
  console.log("==========================================");
  console.log(" Deploying XRPShield Production Contracts ");
  console.log("==========================================");

  const [deployer] = await ethers.getSigners();
  console.log("Deployer Address:", deployer ? deployer.address : "Local Hardhat Signer");

  // 1. Deploy AccessManager
  const AccessManager = await ethers.getContractFactory("AccessManager");
  const accessManager = await AccessManager.deploy(deployer.address);
  await accessManager.waitForDeployment();
  const accessManagerAddress = await accessManager.getAddress();
  console.log("AccessManager deployed to:", accessManagerAddress);

  // 2. Deploy TreasuryStorage
  const TreasuryStorage = await ethers.getContractFactory("TreasuryStorage");
  const treasuryStorage = await TreasuryStorage.deploy();
  await treasuryStorage.waitForDeployment();
  const storageAddress = await treasuryStorage.getAddress();
  console.log("TreasuryStorage deployed to:", storageAddress);

  // 3. Deploy VaultManager
  const VaultManager = await ethers.getContractFactory("VaultManager");
  const vaultManager = await VaultManager.deploy(accessManagerAddress, storageAddress);
  await vaultManager.waitForDeployment();
  const vaultManagerAddress = await vaultManager.getAddress();
  console.log("VaultManager deployed to:", vaultManagerAddress);

  // 4. Authorize VaultManager in TreasuryStorage
  await treasuryStorage.setManagerContract(vaultManagerAddress);
  console.log("TreasuryStorage manager linked to VaultManager successfully.");

  console.log("\nDeployment Summary:");
  console.log("-------------------");
  console.log(`ACCESS_MANAGER_ADDRESS=${accessManagerAddress}`);
  console.log(`TREASURY_STORAGE_ADDRESS=${storageAddress}`);
  console.log(`VAULT_MANAGER_ADDRESS=${vaultManagerAddress}`);
}

main().catch((error) => {
  console.error("Deployment failed:", error);
  process.exitCode = 1;
});
