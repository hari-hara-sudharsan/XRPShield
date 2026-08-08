const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("VaultManager Architecture Suite", function () {
  let accessManager;
  let treasuryStorage;
  let vaultManager;
  let owner;
  let operator;
  let vaultOwner;

  beforeEach(async function () {
    [owner, operator, vaultOwner] = await ethers.getSigners();

    const AccessManager = await ethers.getContractFactory("AccessManager");
    accessManager = await AccessManager.deploy(owner.address);
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
  });

  it("Should deploy cleanly and link dependencies", async function () {
    expect(await vaultManager.accessManager()).to.equal(await accessManager.getAddress());
    expect(await vaultManager.storageContract()).to.equal(await treasuryStorage.getAddress());
  });

  it("Should register a vault and emit VaultRegistered event", async function () {
    const dummyVaultAddr = "0x1111111111111111111111111111111111111111";
    await expect(vaultManager.connect(vaultOwner).registerVault(dummyVaultAddr, "Alpha Treasury Vault", "FXRP"))
      .to.emit(vaultManager, "VaultRegistered")
      .withArgs(dummyVaultAddr, vaultOwner.address, "Alpha Treasury Vault", (await ethers.provider.getBlock("latest")).timestamp + 1);

    const info = await vaultManager.getVault(dummyVaultAddr);
    expect(info.name).to.equal("Alpha Treasury Vault");
    expect(info.owner).to.equal(vaultOwner.address);
    expect(await vaultManager.isVaultActive(dummyVaultAddr)).to.be.true;
  });

  it("Should register execution commitment and record result cleanly", async function () {
    const dummyVaultAddr = "0x6666666666666666666666666666666666666666";
    await vaultManager.connect(vaultOwner).registerVault(dummyVaultAddr, "Execution Vault", "FXRP");

    const decisionHash = ethers.keccak256(ethers.toUtf8Bytes("DECISION_APPROVED_V1"));
    const executionHash = ethers.keccak256(ethers.toUtf8Bytes("EXECUTION_COMPLETED_V1"));

    await expect(vaultManager.connect(vaultOwner).registerExecution(dummyVaultAddr, decisionHash, executionHash, "EXECUTING"))
      .to.emit(vaultManager, "ExecutionRegistered")
      .withArgs(dummyVaultAddr, decisionHash, executionHash, "EXECUTING", (await ethers.provider.getBlock("latest")).timestamp + 1);

    expect(await vaultManager.getLatestExecutionHash(dummyVaultAddr)).to.equal(executionHash);

    await expect(vaultManager.connect(owner).recordExecutionResult(dummyVaultAddr, executionHash, "SUCCESS_RESULT_PAYLOAD", true))
      .to.emit(vaultManager, "ExecutionResultRecorded")
      .withArgs(dummyVaultAddr, executionHash, "SUCCESS_RESULT_PAYLOAD", true, (await ethers.provider.getBlock("latest")).timestamp + 1);
  });
});
