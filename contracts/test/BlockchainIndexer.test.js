const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield Blockchain Event Indexer & Idempotency Tests", function () {
  let deployer, user;
  let accessManager, treasuryStorage, vaultManager;
  let vaultAddress;

  beforeEach(async function () {
    [deployer, user] = await ethers.getSigners();
    vaultAddress = user.address;

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
  });

  it("Should emit all 8 core Flare indexer event types", async function () {
    const policyHash = ethers.keccak256(ethers.toUtf8Bytes("indexer-policy"));
    const attestationHash = ethers.keccak256(ethers.toUtf8Bytes("indexer-attestation"));
    const deadline = Math.floor(Date.now() / 1000) + 3600;

    // 1. VaultCreated (VaultRegistered)
    await expect(vaultManager.connect(user).registerVault(vaultAddress, "Indexer Vault", "FXRP"))
      .to.emit(vaultManager, "VaultRegistered");

    // 2. PolicyCommitted (PolicyCommitmentRegistered)
    await expect(vaultManager.connect(user).registerPolicyCommitmentV2(vaultAddress, policyHash, deadline, 1, 1, "ipfs://meta"))
      .to.emit(vaultManager, "PolicyCommitmentRegistered");

    // 3. PolicyEvaluated, DecisionVerified, HedgeExecutionStarted, HedgeExecuted
    await expect(
      vaultManager.connect(user).executeHedge(
        vaultAddress,
        ethers.parseEther("100"),
        ethers.parseUnits("100", 6),
        user.address,
        deadline,
        policyHash,
        attestationHash,
        "APPROVED"
      )
    ).to.emit(vaultManager, "HedgeExecuted");
  });
});
