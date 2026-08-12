const { ethers } = require("hardhat");
const fs = require("fs");
const path = require("path");
const config = require("../config/extension-config.json");

async function main() {
  console.log("\n========================================================");
  console.log("  Registering XRPShield Extension on TeeExtensionRegistry");
  console.log("========================================================\n");

  const [deployer] = await ethers.getSigners();
  console.log("Deployer Address:", deployer.address);
  console.log("Extension ID:", config.extensionId);
  console.log("Operation Type:", config.operationType);

  // Deploy InstructionSender
  const registryAddress = ethers.getAddress(config.coston2Network.teeRegistryAddress.toLowerCase());
  const XRPShieldInstructionSender = await ethers.getContractFactory("XRPShieldInstructionSender");
  const senderContract = await XRPShieldInstructionSender.deploy(registryAddress, config.extensionId);
  await senderContract.waitForDeployment();

  const senderAddress = await senderContract.getAddress();
  const txHash = senderContract.deploymentTransaction().hash;

  console.log("\n✅ XRPShieldInstructionSender Deployed Successfully!");
  console.log("InstructionSender Address:", senderAddress);
  console.log("Deployment Tx Hash:", txHash);

  const deploymentData = {
    extensionId: config.extensionId,
    operationType: config.operationType,
    instructionSenderAddress: senderAddress,
    transactionHash: txHash,
    registryAddress: registryAddress,
    network: "Flare Coston2 Testnet",
    chainId: 114,
    registeredAt: new Date().toISOString()
  };

  const deploymentFilePath = path.join(__dirname, "fcc-coston2-deployment.json");
  fs.writeFileSync(deploymentFilePath, JSON.stringify(deploymentData, null, 2));
  console.log("\n📄 Registration metadata saved in:", deploymentFilePath);
  console.log("========================================================\n");
}

main().catch((error) => {
  console.error("Extension Registration Failed:", error);
  process.exitCode = 1;
});
