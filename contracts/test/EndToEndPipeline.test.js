const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield End-to-End Pipeline Coston2 Integration Test Suite", function () {
  let deployer, extensionSigner, user;
  let accessManager, treasuryStorage, vaultManager, extensionAdapter;
  let fxrpToken, usdt0Token, routerAdapter;
  let vaultAddress;

  beforeEach(async function () {
    [deployer, extensionSigner, user] = await ethers.getSigners();
    vaultAddress = user.address;

    // 1. Deploy Tokens & Router
    const TestFXRPToken = await ethers.getContractFactory("TestFXRPToken");
    fxrpToken = await TestFXRPToken.deploy();
    await fxrpToken.waitForDeployment();

    const TestUSDT0Token = await ethers.getContractFactory("TestUSDT0Token");
    usdt0Token = await TestUSDT0Token.deploy();
    await usdt0Token.waitForDeployment();

    const DEXRouterAdapter = await ethers.getContractFactory("DEXRouterAdapter");
    routerAdapter = await DEXRouterAdapter.deploy(await fxrpToken.getAddress(), await usdt0Token.getAddress());
    await routerAdapter.waitForDeployment();
    await usdt0Token.transfer(await routerAdapter.getAddress(), ethers.parseUnits("1000000", 6));

    // 2. Deploy FCC Adapter
    const FCCExtensionAdapter = await ethers.getContractFactory("FCCExtensionAdapter");
    extensionAdapter = await FCCExtensionAdapter.deploy(extensionSigner.address);
    await extensionAdapter.waitForDeployment();

    // 3. Deploy Vault Infrastructure
    const AccessManager = await ethers.getContractFactory("AccessManager");
    accessManager = await AccessManager.deploy(deployer.address);
    await accessManager.waitForDeployment();

    const TreasuryStorage = await ethers.getContractFactory("TreasuryStorage");
    treasuryStorage = await TreasuryStorage.deploy();
    await treasuryStorage.waitForDeployment();

    const VaultManager = await ethers.getContractFactory("VaultManager");
    vaultManager = await VaultManager.deploy(
      await accessManager.getAddress(),
      await treasuryStorage.getAddress()
    );
    await vaultManager.waitForDeployment();

    await treasuryStorage.setManagerContract(await vaultManager.getAddress());
    await vaultManager.setFXRPToken(await fxrpToken.getAddress());
    await vaultManager.connect(user).registerVault(vaultAddress, "Primary Vault", "FXRP");
  });

  it("Should complete full 6-step E2E pipeline transaction sequence on Coston2", async function () {
    // Step 1: Read live FTSOv2 price & register policy commitment
    const [price] = await vaultManager.getLatestXRPUSDPrice();
    expect(price).to.be.gt(0);

    const now = Math.floor(Date.now() / 1000);
    const deadline = now + 3600;
    const nonce = 5001;

    const canonicalPayloadStr = JSON.stringify({
      vaultAddress: vaultAddress.toLowerCase(),
      asset: "FXRP",
      hedgeRatio: "1.0000",
      triggerThreshold: "10.0",
      maximumProtection: "100000.0",
      deadline: deadline,
      nonce: nonce,
      policyVersion: 1
    });

    const policyHash = ethers.keccak256(ethers.toUtf8Bytes(canonicalPayloadStr));
    await vaultManager.connect(user).registerPolicyCommitmentV2(
      vaultAddress, policyHash, deadline, nonce, 1, "ipfs://e2e-policy"
    );

    // Step 2 & 3: TEE Enclave Evaluation & EIP-712 Signed ActionResult
    const status = "APPROVED";
    const attestationHash = ethers.keccak256(ethers.toUtf8Bytes("e2e-attestation-proof"));

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

    const value = {
      vaultAddress: vaultAddress,
      policyHash: policyHash,
      status: status,
      attestationHash: attestationHash,
      nonce: nonce,
      timestamp: now,
      deadline: deadline
    };

    const signature = await extensionSigner.signTypedData(domain, types, value);

    // Step 4 & 5: Smart Contract Gatekeeper Verification & DEX Swap Execution
    const fxrpAmountIn = ethers.parseEther("100");
    const minUsdtOut = ethers.parseUnits("101", 6);

    const isVerified = await vaultManager.verifyDecision(vaultAddress, policyHash, status, attestationHash);
    expect(isVerified).to.be.true;

    await expect(
      vaultManager.connect(user).executeHedge(
        vaultAddress,
        fxrpAmountIn,
        minUsdtOut,
        await routerAdapter.getAddress(),
        deadline,
        policyHash,
        attestationHash,
        status
      )
    ).to.emit(vaultManager, "HedgeExecuted")
     .withArgs(vaultAddress, fxrpAmountIn, minUsdtOut, attestationHash);

    // Step 6: Verify duplicate execution rejection
    await expect(
      vaultManager.connect(user).executeHedge(
        vaultAddress,
        fxrpAmountIn,
        minUsdtOut,
        await routerAdapter.getAddress(),
        deadline,
        policyHash,
        attestationHash,
        status
      )
    ).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");
  });
});
