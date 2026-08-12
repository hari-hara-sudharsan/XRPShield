const { ethers } = require("hardhat");
const fs = require("fs");
const path = require("path");

async function main() {
  console.log("\n========================================================");
  console.log("  Deploying XRPShieldVault to Flare Coston2 Testnet   ");
  console.log("========================================================\n");

  const [deployer] = await ethers.getSigners();
  console.log("Deployer Wallet Address:", deployer.address);

  const balance = await deployer.provider.getBalance(deployer.address);
  console.log("Deployer Native Balance:", ethers.formatEther(balance), "CFLR");

  const registryAddress = ethers.getAddress("0xaD6740B4F817109E96238bA722880b91e92dEec9".toLowerCase());
  const fxrpTokenAddress = ethers.getAddress("0x0d37e61a681dcf690ff33e7fd2918809989f664a".toLowerCase());

  console.log("Flare Contract Registry Address:", registryAddress);
  console.log("Default FXRP Token Address:", fxrpTokenAddress);

  // Deploy XRPShieldVault
  const XRPShieldVault = await ethers.getContractFactory("XRPShieldVault");
  const vaultContract = await XRPShieldVault.deploy(registryAddress, fxrpTokenAddress);
  await vaultContract.waitForDeployment();

  const contractAddress = await vaultContract.getAddress();
  const txHash = vaultContract.deploymentTransaction().hash;
  const receipt = await vaultContract.deploymentTransaction().wait();

  console.log("\n✅ XRPShieldVault Deployed Successfully!");
  console.log("Contract Address:", contractAddress);
  console.log("Deployment Tx Hash:", txHash);
  console.log("Deployed Block Number:", receipt.blockNumber);
  console.log("Gas Used:", receipt.gasUsed.toString());

  // Record deployment metadata
  const deploymentsDir = path.join(__dirname, "../deployments");
  if (!fs.existsSync(deploymentsDir)) {
    fs.mkdirSync(deploymentsDir, { recursive: true });
  }

  const deploymentData = {
    contractName: "XRPShieldVault",
    contractAddress: contractAddress,
    transactionHash: txHash,
    blockNumber: receipt.blockNumber,
    gasUsed: receipt.gasUsed.toString(),
    network: "Flare Coston2 Testnet",
    chainId: 114,
    compilerVersion: "solc 0.8.20",
    constructorArguments: [registryAddress, fxrpTokenAddress],
    deployedAt: new Date().toISOString()
  };

  const deploymentFilePath = path.join(deploymentsDir, "coston2.json");
  fs.writeFileSync(deploymentFilePath, JSON.stringify(deploymentData, null, 2));
  console.log("\n📄 Deployment metadata recorded in:", deploymentFilePath);
  console.log("========================================================\n");
}

main().catch((error) => {
  console.error("Deployment Failed:", error);
  process.exitCode = 1;
});
