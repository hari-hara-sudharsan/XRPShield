const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield Real On-Chain FCC Instruction Routing Unit Tests", function () {
  let vaultContract;
  let fxrpToken;
  let owner;
  let user1;
  let user2;
  let vaultId;
  let policyCommitment;

  beforeEach(async function () {
    [owner, user1, user2] = await ethers.getSigners();

    // 1. Deploy Test FXRP Token
    const TestFXRPToken = await ethers.getContractFactory("TestFXRPToken");
    fxrpToken = await TestFXRPToken.deploy();
    await fxrpToken.waitForDeployment();

    // 2. Deploy XRPShieldVault
    const XRPShieldVault = await ethers.getContractFactory("XRPShieldVault");
    vaultContract = await XRPShieldVault.deploy(
      ethers.ZeroAddress,
      await fxrpToken.getAddress()
    );
    await vaultContract.waitForDeployment();

    // Create vault for user1
    const tx = await vaultContract.connect(user1).createVault(user1.address, "FCC Test Vault");
    const receipt = await tx.wait();
    const event = receipt.logs.find(log => log.fragment && log.fragment.name === "VaultCreated");
    vaultId = event.args.vaultId;

    policyCommitment = ethers.keccak256(ethers.toUtf8Bytes("canonical-fcc-policy-v1"));
  });

  it("1. Should successfully submit policy evaluation instruction on-chain", async function () {
    const deadline = Math.floor(Date.now() / 1000) + 3600;
    const currentPrice = ethers.parseUnits("0.85", 8);

    await expect(
      vaultContract.connect(user1).requestPolicyEvaluation(vaultId, policyCommitment, currentPrice, deadline)
    ).to.emit(vaultContract, "PolicyEvaluationRequested")
      .withArgs(vaultId, policyCommitment, (val) => val !== ethers.ZeroHash, (ts) => ts > 0);
  });

  it("2. Should reject instruction request from non-owner caller", async function () {
    const deadline = Math.floor(Date.now() / 1000) + 3600;
    const currentPrice = ethers.parseUnits("0.85", 8);

    await expect(
      vaultContract.connect(user2).requestPolicyEvaluation(vaultId, policyCommitment, currentPrice, deadline)
    ).to.be.revertedWithCustomError(vaultContract, "UnauthorizedCaller");
  });

  it("3. Should reject instruction request for zero policy commitment hash", async function () {
    const deadline = Math.floor(Date.now() / 1000) + 3600;
    const currentPrice = ethers.parseUnits("0.85", 8);

    await expect(
      vaultContract.connect(user1).requestPolicyEvaluation(vaultId, ethers.ZeroHash, currentPrice, deadline)
    ).to.be.revertedWithCustomError(vaultContract, "InvalidParameters");
  });

  it("4. Should reject instruction request with expired deadline", async function () {
    const expiredDeadline = Math.floor(Date.now() / 1000) - 600; // 10 mins ago
    const currentPrice = ethers.parseUnits("0.85", 8);

    await expect(
      vaultContract.connect(user1).requestPolicyEvaluation(vaultId, policyCommitment, currentPrice, expiredDeadline)
    ).to.be.revertedWithCustomError(vaultContract, "PolicyExpired");
  });

  it("5. Should store instruction record with STATUS = REQUESTED", async function () {
    const deadline = Math.floor(Date.now() / 1000) + 3600;
    const currentPrice = ethers.parseUnits("0.85", 8);

    const tx = await vaultContract.connect(user1).requestPolicyEvaluation(vaultId, policyCommitment, currentPrice, deadline);
    const receipt = await tx.wait();
    const event = receipt.logs.find(log => log.fragment && log.fragment.name === "PolicyEvaluationRequested");
    const instructionId = event.args.instructionId;

    const record = await vaultContract.instructions(instructionId);
    expect(record.vaultId).to.equal(vaultId);
    expect(record.policyCommitment).to.equal(policyCommitment);
    expect(record.status).to.equal("REQUESTED");
  });
});
