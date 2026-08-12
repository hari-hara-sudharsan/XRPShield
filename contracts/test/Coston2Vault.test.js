const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield Coston2 FXRP Vault Unit & Integration Tests", function () {
  let deployer, user;
  let fxrpToken, accessManager, treasuryStorage, vaultManager;

  beforeEach(async function () {
    [deployer, user] = await ethers.getSigners();

    // 1. Deploy TestFXRPToken
    const TestFXRPToken = await ethers.getContractFactory("TestFXRPToken");
    fxrpToken = await TestFXRPToken.deploy();
    await fxrpToken.waitForDeployment();

    // 2. Deploy AccessManager
    const AccessManager = await ethers.getContractFactory("AccessManager");
    accessManager = await AccessManager.deploy(deployer.address);
    await accessManager.waitForDeployment();

    // 3. Deploy TreasuryStorage
    const TreasuryStorage = await ethers.getContractFactory("TreasuryStorage");
    treasuryStorage = await TreasuryStorage.deploy();
    await treasuryStorage.waitForDeployment();

    // 4. Deploy VaultManager
    const VaultManager = await ethers.getContractFactory("VaultManager");
    vaultManager = await VaultManager.deploy(
      await accessManager.getAddress(),
      await treasuryStorage.getAddress()
    );
    await vaultManager.waitForDeployment();

    // 5. Configure VaultManager
    await treasuryStorage.setManagerContract(await vaultManager.getAddress());
    await vaultManager.setFXRPToken(await fxrpToken.getAddress());
  });

  it("Should mint testnet FXRP reserves and verify initial balance", async function () {
    const depositAmount = ethers.parseEther("1000");
    await fxrpToken.mint(user.address, depositAmount);

    const userBalance = await fxrpToken.balanceOf(user.address);
    expect(userBalance).to.equal(depositAmount);
  });

  it("Should approve and deposit FXRP reserves into VaultManager on-chain", async function () {
    const depositAmount = ethers.parseEther("500");
    await fxrpToken.mint(user.address, depositAmount);

    // Approve VaultManager
    await fxrpToken.connect(user).approve(await vaultManager.getAddress(), depositAmount);

    // Deposit to VaultManager
    await expect(vaultManager.connect(user).depositFXRP(depositAmount))
      .to.emit(vaultManager, "DepositExecuted")
      .withArgs(user.address, depositAmount, (await ethers.provider.getBlock('latest')).timestamp + 1);

    const vaultBalance = await vaultManager.getUserFXRPBalance(user.address);
    expect(vaultBalance).to.equal(depositAmount);

    const totalReserves = await vaultManager.totalFXRPReserves();
    expect(totalReserves).to.equal(depositAmount);
  });

  it("Should withdraw FXRP reserves from VaultManager and update state", async function () {
    const initialDeposit = ethers.parseEther("1000");
    const withdrawAmount = ethers.parseEther("400");
    await fxrpToken.mint(user.address, initialDeposit);

    await fxrpToken.connect(user).approve(await vaultManager.getAddress(), initialDeposit);
    await vaultManager.connect(user).depositFXRP(initialDeposit);

    // Withdraw
    await expect(vaultManager.connect(user).withdrawFXRP(withdrawAmount))
      .to.emit(vaultManager, "WithdrawalExecuted");

    const remainingVaultBalance = await vaultManager.getUserFXRPBalance(user.address);
    expect(remainingVaultBalance).to.equal(ethers.parseEther("600"));

    const userWalletBalance = await fxrpToken.balanceOf(user.address);
    expect(userWalletBalance).to.equal(withdrawAmount);
  });
});
