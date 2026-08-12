const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield Production Safety Controls & Circuit Breaker Test Suite", function () {
  let deployer, extensionSigner, pauser, attacker, user;
  let accessManager, treasuryStorage, vaultManager, extensionAdapter;
  let fxrpToken, usdt0Token, routerAdapter;
  let vaultAddress;

  beforeEach(async function () {
    [deployer, extensionSigner, pauser, attacker, user] = await ethers.getSigners();
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

    // 2. Deploy Infrastructure & Roles
    const AccessManager = await ethers.getContractFactory("AccessManager");
    accessManager = await AccessManager.deploy(deployer.address);
    await accessManager.waitForDeployment();

    await accessManager.grantPauserRole(pauser.address);

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

  it("1. Emergency Pause Circuit Breaker: Freezes execution when paused", async function () {
    await vaultManager.connect(pauser).pauseExecution();

    const now = Math.floor(Date.now() / 1000);
    const deadline = now + 3600;

    await expect(
      vaultManager.connect(user).executeHedge(
        vaultAddress,
        ethers.parseEther("100"),
        ethers.parseUnits("100", 6),
        await routerAdapter.getAddress(),
        deadline,
        ethers.keccak256(ethers.toUtf8Bytes("policy")),
        ethers.keccak256(ethers.toUtf8Bytes("attestation")),
        "APPROVED"
      )
    ).to.be.revertedWithCustomError(vaultManager, "EnforcedPause");
  });

  it("2. Execution Cooldown Safeguard: Rejects consecutive hedges within 300s", async function () {
    const now = Math.floor(Date.now() / 1000);
    const deadline = now + 3600;
    const nonce = 9001;

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
      vaultAddress, policyHash, deadline, nonce, 1, "ipfs://cooldown-policy"
    );

    const attestationHash1 = ethers.keccak256(ethers.toUtf8Bytes("attestation-1"));
    const fxrpAmountIn = ethers.parseEther("100");
    const minUsdtOut = ethers.parseUnits("100", 6);

    // First execution succeeds
    await vaultManager.connect(user).executeHedge(
      vaultAddress,
      fxrpAmountIn,
      minUsdtOut,
      await routerAdapter.getAddress(),
      deadline,
      policyHash,
      attestationHash1,
      "APPROVED"
    );

    // Immediate second execution within cooldown period reverts
    const attestationHash2 = ethers.keccak256(ethers.toUtf8Bytes("attestation-2"));
    await expect(
      vaultManager.connect(user).executeHedge(
        vaultAddress,
        fxrpAmountIn,
        minUsdtOut,
        await routerAdapter.getAddress(),
        deadline,
        policyHash,
        attestationHash2,
        "APPROVED"
      )
    ).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");
  });

  it("3. Unauthorized Caller Safeguard: Rejects attacker calls", async function () {
    const now = Math.floor(Date.now() / 1000);
    const deadline = now + 3600;

    await expect(
      vaultManager.connect(attacker).executeHedge(
        vaultAddress,
        ethers.parseEther("100"),
        ethers.parseUnits("100", 6),
        await routerAdapter.getAddress(),
        deadline,
        ethers.keccak256(ethers.toUtf8Bytes("policy")),
        ethers.keccak256(ethers.toUtf8Bytes("attestation")),
        "APPROVED"
      )
    ).to.be.revertedWithCustomError(vaultManager, "UnauthorizedCaller");
  });

  it("4. Emergency Withdrawal when Paused: Transfers funds safely to owner", async function () {
    await fxrpToken.transfer(vaultAddress, ethers.parseEther("500"));
    await fxrpToken.connect(user).approve(await vaultManager.getAddress(), ethers.parseEther("500"));
    await vaultManager.connect(user).depositFXRP(ethers.parseEther("500"));

    // Freeze system
    await vaultManager.connect(pauser).pauseExecution();

    // Withdraw in emergency
    await expect(
      vaultManager.connect(user).emergencyWithdrawFXRP(vaultAddress, ethers.parseEther("500"))
    ).to.emit(vaultManager, "EmergencyWithdrawalExecuted")
     .withArgs(vaultAddress, user.address, ethers.parseEther("500"));
  });
});
