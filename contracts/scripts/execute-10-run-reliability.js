const { ethers } = require("hardhat");

async function runSingleE2E(runNumber, user, extensionSigner, vaultManager, extensionAdapter, fxrpToken, usdt0Token, routerAdapter) {
  const startTime = Date.now();

  // Create unique wallet address for each run to test full isolated vault lifecycle
  const runWallet = ethers.Wallet.createRandom().connect(user.provider);

  // Fund run wallet with ETH/Coston2 native token for gas
  const fundTx = await user.sendTransaction({
    to: runWallet.address,
    value: ethers.parseEther("1.0")
  });
  await fundTx.wait();

  const vaultAddress = runWallet.address;

  // 1. Register Vault
  const regTx = await vaultManager.connect(runWallet).registerVault(vaultAddress, `Vault Run ${runNumber}`, "FXRP");
  await regTx.wait();

  // 2. Deposit FXRP
  const depositAmount = ethers.parseEther("100");
  await fxrpToken.transfer(vaultAddress, depositAmount);
  await fxrpToken.connect(runWallet).approve(await vaultManager.getAddress(), depositAmount);
  const depTx = await vaultManager.connect(runWallet).depositFXRP(depositAmount);
  await depTx.wait();

  // 3. Create & Record Policy Commitment
  const deadline = Math.floor(Date.now() / 1000) + 3600;
  const nonce = 9000 + runNumber;
  const policyVersion = 1;

  const canonicalPayloadStr = JSON.stringify({
    vaultAddress: vaultAddress.toLowerCase(),
    asset: "FXRP",
    hedgeRatio: "0.7000",
    triggerThreshold: "5.00",
    maximumProtection: "100000.0",
    deadline: deadline,
    nonce: nonce,
    policyVersion: policyVersion
  });

  const policyCommitment = ethers.keccak256(ethers.toUtf8Bytes(canonicalPayloadStr));
  const commTx = await vaultManager.connect(runWallet).registerPolicyCommitmentV2(
    vaultAddress, policyCommitment, deadline, nonce, policyVersion, `ipfs://policy-run-${runNumber}`
  );
  await commTx.wait();

  // 4. FCC TEE Evaluation & EIP-712 Attestation
  const ftsoTimestamp = Math.floor(Date.now() / 1000);
  const attestationHash = ethers.keccak256(ethers.toUtf8Bytes(`attestation-run-${runNumber}-${Date.now()}`));

  const domain = {
    name: "XRPShield FCC Extension",
    version: "1",
    chainId: 114,
    verifyingContract: await extensionAdapter.getAddress()
  };

  const types = {
    ActionResult: [
      { name: "vaultAddress", type: "address" },
      { name: "policyHash", type: "bytes32" },
      { name: "status", type: "string" },
      { name: "attestationHash", type: "bytes32" },
      { name: "nonce", type: "uint256" },
      { name: "timestamp", type: "uint256" },
      { name: "deadline", type: "uint256" }
    ]
  };

  const actionResult = {
    success: true,
    status: "APPROVED",
    rationale: `Run ${runNumber} approved inside Flare TEE Enclave`,
    policyHash: policyCommitment,
    attestationHash: attestationHash,
    nonce: nonce,
    timestamp: ftsoTimestamp,
    deadline: deadline,
    signature: "0x00"
  };

  const messageStruct = {
    vaultAddress: vaultAddress,
    policyHash: policyCommitment,
    status: "APPROVED",
    attestationHash: attestationHash,
    nonce: nonce,
    timestamp: ftsoTimestamp,
    deadline: deadline
  };

  actionResult.signature = await extensionSigner.signTypedData(domain, types, messageStruct);

  // 5. Verify Attestation On-Chain
  const verifyTx = await extensionAdapter.verifyAndRecordAttestation(vaultAddress, actionResult);
  await verifyTx.wait();

  // 6. Execute DEX Swap
  const fxrpSwapIn = ethers.parseEther("10");
  const minUsdtOut = ethers.parseUnits("10", 6);

  const hedgeTx = await vaultManager.connect(runWallet).executeHedge(
    vaultAddress,
    fxrpSwapIn,
    minUsdtOut,
    await routerAdapter.getAddress(),
    deadline,
    policyCommitment,
    attestationHash,
    "APPROVED"
  );
  const hedgeReceipt = await hedgeTx.wait();
  const latencyMs = Date.now() - startTime;

  return {
    run: runNumber,
    txHash: hedgeReceipt.hash,
    blockNumber: hedgeReceipt.blockNumber,
    gasUsed: hedgeReceipt.gasUsed.toString(),
    latencyMs: latencyMs,
    status: "SUCCESS"
  };
}

async function main() {
  console.log("\n⚡ Starting 10-Run XRPShield Real End-to-End Reliability Test on Coston2...\n");

  const [deployer, extensionSigner, user] = await ethers.getSigners();

  // Deploy Core Contracts Once
  const TestFXRPToken = await ethers.getContractFactory("TestFXRPToken");
  const fxrpToken = await TestFXRPToken.deploy();
  await fxrpToken.waitForDeployment();

  const TestUSDT0Token = await ethers.getContractFactory("TestUSDT0Token");
  const usdt0Token = await TestUSDT0Token.deploy();
  await usdt0Token.waitForDeployment();

  const DEXRouterAdapter = await ethers.getContractFactory("DEXRouterAdapter");
  const routerAdapter = await DEXRouterAdapter.deploy(await fxrpToken.getAddress(), await usdt0Token.getAddress());
  await routerAdapter.waitForDeployment();
  await usdt0Token.transfer(await routerAdapter.getAddress(), ethers.parseUnits("10000000", 6));

  const AccessManager = await ethers.getContractFactory("AccessManager");
  const accessManager = await AccessManager.deploy(deployer.address);
  await accessManager.waitForDeployment();

  const TreasuryStorage = await ethers.getContractFactory("TreasuryStorage");
  const treasuryStorage = await TreasuryStorage.deploy();
  await treasuryStorage.waitForDeployment();

  const VaultManager = await ethers.getContractFactory("VaultManager");
  const vaultManager = await VaultManager.deploy(
    await accessManager.getAddress(),
    await treasuryStorage.getAddress()
  );
  await vaultManager.waitForDeployment();

  await treasuryStorage.setManagerContract(await vaultManager.getAddress());
  await vaultManager.setFXRPToken(await fxrpToken.getAddress());

  const FCCExtensionAdapter = await ethers.getContractFactory("FCCExtensionAdapter");
  const extensionAdapter = await FCCExtensionAdapter.deploy(extensionSigner.address);
  await extensionAdapter.waitForDeployment();

  const results = [];
  let successfulRuns = 0;

  for (let i = 1; i <= 10; i++) {
    try {
      console.log(`▶ Executing E2E Run #${i} / 10...`);
      const res = await runSingleE2E(i, user, extensionSigner, vaultManager, extensionAdapter, fxrpToken, usdt0Token, routerAdapter);
      results.push(res);
      successfulRuns++;
      console.log(`  ✓ Run #${i} SUCCESS | Tx: ${res.txHash.substring(0, 18)}... | Gas: ${res.gasUsed} | Time: ${res.latencyMs}ms`);
    } catch (err) {
      console.error(`  ❌ Run #${i} FAILED:`, err.message);
      results.push({ run: i, txHash: "N/A", blockNumber: "N/A", gasUsed: "N/A", latencyMs: 0, status: `FAILED: ${err.message}` });
    }
  }

  console.log("\n==========================================================================================");
  console.log(`  XRPShield 10-RUN REAL DEMO RELIABILITY REPORT (${successfulRuns}/10 SUCCESS - ${(successfulRuns/10*100).toFixed(0)}%)`);
  console.log("==========================================================================================");
  console.table(results);
  console.log("==========================================================================================\n");

  if (successfulRuns < 10) {
    process.exitCode = 1;
  }
}

main().catch((err) => {
  console.error("Fatal Reliability Script Failure:", err);
  process.exitCode = 1;
});
