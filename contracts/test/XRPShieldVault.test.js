const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShieldVault Smart Contract Unit Tests", function () {
  let vaultContract;
  let fxrpToken;
  let owner;
  let user1;
  let user2;
  let vaultId;

  beforeEach(async function () {
    [owner, user1, user2] = await ethers.getSigners();

    // 1. Deploy Test FXRP Token
    const TestFXRPToken = await ethers.getContractFactory("TestFXRPToken");
    fxrpToken = await TestFXRPToken.deploy();
    await fxrpToken.waitForDeployment();

    // 2. Deploy XRPShieldVault
    const XRPShieldVault = await ethers.getContractFactory("XRPShieldVault");
    vaultContract = await XRPShieldVault.deploy(
      ethers.ZeroAddress, // No registry mock needed for direct address test
      await fxrpToken.getAddress()
    );
    await vaultContract.waitForDeployment();

    // Mint FXRP to user1 & approve vault
    const depositAmount = ethers.parseEther("1000");
    await fxrpToken.transfer(user1.address, depositAmount);
    await fxrpToken.connect(user1).approve(await vaultContract.getAddress(), depositAmount);

    // Create vault for user1
    const tx = await vaultContract.connect(user1).createVault(user1.address, "Alpha Corporate Treasury");
    const receipt = await tx.wait();
    const event = receipt.logs.find(log => log.fragment && log.fragment.name === "VaultCreated");
    vaultId = event.args.vaultId;
  });

  it("1. Should successfully create an isolated treasury vault", async function () {
    expect(await vaultContract.getVaultOwner(vaultId)).to.equal(user1.address);
    expect(await vaultContract.getVaultStatus(vaultId)).to.equal("ACTIVE");
    expect(await vaultContract.getVaultBalance(vaultId)).to.equal(0);
  });

  it("2. Should successfully custody FXRP deposit into vault", async function () {
    const depositAmount = ethers.parseEther("500");
    await expect(vaultContract.connect(user1).depositFXRP(vaultId, depositAmount))
      .to.emit(vaultContract, "FXRPDeposited")
      .withArgs(vaultId, user1.address, depositAmount, depositAmount, (val) => val > 0);

    expect(await vaultContract.getVaultBalance(vaultId)).to.equal(depositAmount);
  });

  it("3. Should successfully withdraw FXRP when called by vault owner", async function () {
    const depositAmount = ethers.parseEther("500");
    await vaultContract.connect(user1).depositFXRP(vaultId, depositAmount);

    const withdrawAmount = ethers.parseEther("200");
    await expect(vaultContract.connect(user1).withdrawFXRP(vaultId, withdrawAmount, user1.address))
      .to.emit(vaultContract, "FXRPWithdrawn")
      .withArgs(vaultId, user1.address, withdrawAmount, ethers.parseEther("300"), (val) => val > 0);

    expect(await vaultContract.getVaultBalance(vaultId)).to.equal(ethers.parseEther("300"));
  });

  it("4. Should reject unauthorized withdrawal attempt by non-owner", async function () {
    const depositAmount = ethers.parseEther("500");
    await vaultContract.connect(user1).depositFXRP(vaultId, depositAmount);

    await expect(
      vaultContract.connect(user2).withdrawFXRP(vaultId, ethers.parseEther("100"), user2.address)
    ).to.be.revertedWithCustomError(vaultContract, "UnauthorizedCaller");
  });

  it("5. Should reject zero amount deposit", async function () {
    await expect(
      vaultContract.connect(user1).depositFXRP(vaultId, 0)
    ).to.be.revertedWithCustomError(vaultContract, "ZeroAmount");
  });

  it("6. Should reject zero amount withdrawal", async function () {
    await expect(
      vaultContract.connect(user1).withdrawFXRP(vaultId, 0, user1.address)
    ).to.be.revertedWithCustomError(vaultContract, "ZeroAmount");
  });

  it("7. Should reject operations against an invalid vault ID", async function () {
    const fakeVaultId = ethers.keccak256(ethers.toUtf8Bytes("invalid-vault"));
    await expect(
      vaultContract.getVaultBalance(fakeVaultId)
    ).to.be.revertedWithCustomError(vaultContract, "InvalidVault");
  });

  it("8. Should reject withdrawal exceeding current vault reserves", async function () {
    const depositAmount = ethers.parseEther("100");
    await vaultContract.connect(user1).depositFXRP(vaultId, depositAmount);

    await expect(
      vaultContract.connect(user1).withdrawFXRP(vaultId, ethers.parseEther("500"), user1.address)
    ).to.be.revertedWithCustomError(vaultContract, "InsufficientVaultBalance");
  });

  it("9. Should reject zero address owner when creating vault", async function () {
    await expect(
      vaultContract.createVault(ethers.ZeroAddress, "Zero Owner Vault")
    ).to.be.revertedWithCustomError(vaultContract, "ZeroOwnerAddress");
  });
});
