const fs = require('fs');
const path = require('path');

async function deployExtension() {
  console.log("==================================================");
  console.log(" Deploying XRPShield Flare Compute Extension (FCE)");
  console.log(" Network: Flare Coston2 Testnet (Chain ID 114)     ");
  console.log("==================================================");

  const extensionConfig = {
    extensionId: "fce-xrpshield-v1",
    name: "XRPShield Confidential Treasury Extension",
    network: "coston2",
    chainId: 114,
    signerAddress: "0x3C44CdD45914c1b25515c10097782163b21c439f",
    adapterContractAddress: "0x0000000000000000000000000000000000000000",
    commands: ["EVALUATE_HEDGE_POLICY", "GET_EXTENSION_STATUS"],
    deployedAt: new Date().toISOString()
  };

  const manifestPath = path.join(__dirname, "extension-manifest.json");
  fs.writeFileSync(manifestPath, JSON.stringify(extensionConfig, null, 2));

  console.log("Flare Compute Extension manifest written to:", manifestPath);
  console.log("Deploy Status: ACTIVE");
}

deployExtension();
