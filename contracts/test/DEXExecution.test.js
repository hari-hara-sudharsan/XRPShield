const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield Real On-Chain Coston2 DEX Swap Execution Tests", function () {
  let deployer, user;
  let accessManager, treasuryStorage, vaultManager;
  let fxrpToken, usdt0Token, routerAdapter;
  let vaultAddress;

  beforeEach(async function () {
    [deployer, user] = await ethers.getSigners();
    vaultAddress = user.address;

    // 1. Deploy Tokens
    const TestFXRPToken = await ethers.getContractFactory("TestFXRPToken");
    fxrpToken = await TestFXRPToken.deploy();
    await fxrpToken.waitForDeployment();

    const TestUSDT0Token = await ethers.getContractFactory("TestUSDT0Token");
    usdt0Token = await TestUSDT0Token.deploy();
    await usdt0Token.waitForDeployment();

    // 2. Deploy DEX Router Adapter
    const DEXRouterAdapter = await ethers.getContractFactory("DEXRouterAdapter");
    routerAdapter = await DEXRouterAdapter.deploy(await fxrpToken.getAddress(), await usdt0Token.getAddress());
    await routerAdapter.waitForDeployment();

    // Fund router with USDT0 reserves
    await usdt0Token.transfer(await routerAdapter.getAddress(), ethers.parseUnits("1000000", 6));

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

    // Register user vault and fund with 1,000 FXRP
    await vaultManager.connect(user).registerVault(vaultAddress, "Primary Vault", "FXRP");
    await fxrpToken.transfer(vaultAddress, ethers.parseEther("1000"));
  });

  it("Should execute real FXRP -> USDT0 DEX swap on Coston2 Router", async function () {
    const fxrpAmountIn = ethers.parseEther("100"); // 100 FXRP
    const minUsdtOut = ethers.parseUnits("101", 6); // 101 USDT0 (0.5% slippage from 102.25)
    const deadline = Math.floor(Date.now() / 1000) + 1200;

    // Approve VaultManager to transfer FXRP from user
    await fxrpToken.connect(user).approve(await vaultManager.getAddress(), fxrpAmountIn);

    // Execute DEX swap through VaultManager
    await expect(
      vaultManager.connect(user).executeHedgeSwap(
        vaultAddress,
        fxrpAmountIn,
        minUsdtOut,
        await routerAdapter.getAddress(),
        deadline
      )
    ).to.emit(vaultManager, "PolicyAttestationRecorded");
  });

  it("Should REJECT swap if deadline is expired", async function () {
    const fxrpAmountIn = ethers.parseEther("100");
    const minUsdtOut = ethers.parseUnits("101", 6);
    const expiredDeadline = Math.floor(Date.now() / 1000) - 600;

    await expect(
      vaultManager.connect(user).executeHedgeSwap(
        vaultAddress,
        fxrpAmountIn,
        minUsdtOut,
        await routerAdapter.getAddress(),
        expiredDeadline
      )
    ).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");
  });
});
