const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield Phase 3 Sprint 9: Hostile Execution Attack Test Suite", function () {
  let vaultContract;
  let hedgeExecutor;
  let adapter;
  let fxrpToken;
  let owner;
  let teeSigner;
  let user1;
  let user2;
  let vaultId;
  let policyCommitment;
  let instructionId;
  let chainId;
  const dexRouterAddress = "0x600109D9cDe3267E1408892f39C27DBdf8dd6B4B";

  beforeEach(async function () {
    [owner, teeSigner, user1, user2] = await ethers.getSigners();

    const TestFXRPToken = await ethers.getContractFactory("TestFXRPToken");
    fxrpToken = await TestFXRPToken.deploy();
    await fxrpToken.waitForDeployment();

    const XRPShieldVault = await ethers.getContractFactory("XRPShieldVault");
    vaultContract = await XRPShieldVault.deploy(ethers.ZeroAddress, await fxrpToken.getAddress());
    await vaultContract.waitForDeployment();

    const FCCExtensionAdapter = await ethers.getContractFactory("FCCExtensionAdapter");
    adapter = await FCCExtensionAdapter.deploy(teeSigner.address);
    await adapter.waitForDeployment();

    const HedgeExecutor = await ethers.getContractFactory("HedgeExecutor");
    hedgeExecutor = await HedgeExecutor.deploy(dexRouterAddress);
    await hedgeExecutor.waitForDeployment();

    await vaultContract.setFccAdapterAddress(await adapter.getAddress());
    await vaultContract.setHedgeExecutorAddress(await hedgeExecutor.getAddress());

    const net = await ethers.provider.getNetwork();
    chainId = Number(net.chainId);

    // Create Vault & Deposit FXRP
    const tx = await vaultContract.connect(user1).createVault(user1.address, "Hostile Test Vault");
    const receipt = await tx.wait();
    const event = receipt.logs.find(log => log.fragment && log.fragment.name === "VaultCreated");
    vaultId = event.args.vaultId;

    await fxrpToken.mint(user1.address, ethers.parseEther("100000"));
    await fxrpToken.connect(user1).approve(await vaultContract.getAddress(), ethers.parseEther("100000"));
    await vaultContract.connect(user1).depositFXRP(vaultId, ethers.parseEther("100000"));

    policyCommitment = ethers.keccak256(ethers.toUtf8Bytes("hostile-fcc-policy-v1"));

    const reqTx = await vaultContract.connect(user1).requestPolicyEvaluation(
      vaultId,
      policyCommitment,
      ethers.parseUnits("0.85", 8),
      Math.floor(Date.now() / 1000) + 3600
    );
    const reqReceipt = await reqTx.wait();
    const reqEvent = reqReceipt.logs.find(log => log.fragment && log.fragment.name === "PolicyEvaluationRequested");
    instructionId = reqEvent.args.instructionId;
  });

  async function verifyAttestationForUser(signer, statusStr = "APPROVED") {
    const timestamp = Math.floor(Date.now() / 1000);
    const deadline = timestamp + 3600;
    const attestationHash = ethers.keccak256(
      ethers.AbiCoder.defaultAbiCoder().encode(
        ["address", "bytes32", "string", "uint256", "uint256"],
        [user1.address, policyCommitment, statusStr, 901, timestamp]
      )
    );

    const domain = { name: "XRPShield FCC Extension", version: "1", chainId: chainId, verifyingContract: adapter.target };
    const types = { ActionResult: [{ name: "vaultAddress", type: "address" }, { name: "policyHash", type: "bytes32" }, { name: "status", type: "string" }, { name: "attestationHash", type: "bytes32" }, { name: "nonce", type: "uint256" }, { name: "timestamp", type: "uint256" }, { name: "deadline", type: "uint256" }] };
    const value = { vaultAddress: user1.address, policyHash: policyCommitment, status: statusStr, attestationHash: attestationHash, nonce: 901, timestamp: timestamp, deadline: deadline };

    const signature = await signer.signTypedData(domain, types, value);
    const actionResult = { success: true, status: statusStr, rationale: "Triggered", policyHash: policyCommitment, attestationHash: attestationHash, nonce: 901, timestamp: timestamp, deadline: deadline, signature: signature };

    return vaultContract.submitPolicyEvaluationResult(instructionId, vaultId, policyCommitment, statusStr, ethers.parseEther("10000"), actionResult);
  }

  it("1. Attack 1 (No FCC Approval): Should REVERT direct executeHedge() call", async function () {
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const params = { vaultId, policyCommitment, instructionId, amountIn: ethers.parseEther("1000"), minimumAmountOut: ethers.parseUnits("850", 6), deadline: Math.floor(Date.now() / 1000) + 3600, route, verifiedDecision: "APPROVED" };
    await expect(vaultContract.connect(user1).executeHedge(params)).to.be.revertedWithCustomError(vaultContract, "AttestationVerificationFailed");
  });

  it("2. Attack 2 (Invalid Attestation Signature): Should REVERT submitPolicyEvaluationResult()", async function () {
    await expect(verifyAttestationForUser(user2)).to.be.revertedWithCustomError(vaultContract, "AttestationVerificationFailed");
  });

  it("3. Attack 3 (Mismatched Policy Commitment): Should REVERT executeHedge()", async function () {
    await verifyAttestationForUser(teeSigner);
    const badCommitment = ethers.keccak256(ethers.toUtf8Bytes("fake-policy-hash"));
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const params = { vaultId, policyCommitment: badCommitment, instructionId, amountIn: ethers.parseEther("1000"), minimumAmountOut: ethers.parseUnits("850", 6), deadline: Math.floor(Date.now() / 1000) + 3600, route, verifiedDecision: "APPROVED" };
    await expect(vaultContract.connect(user1).executeHedge(params)).to.be.revertedWithCustomError(vaultContract, "InvalidPolicyCommitment");
  });

  it("4. Attack 4 (Mismatched Vault Address): Should REVERT executeHedge()", async function () {
    await verifyAttestationForUser(teeSigner);
    const badVaultId = ethers.keccak256(ethers.toUtf8Bytes("unauthorized-vault"));
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const params = { vaultId: badVaultId, policyCommitment, instructionId, amountIn: ethers.parseEther("1000"), minimumAmountOut: ethers.parseUnits("850", 6), deadline: Math.floor(Date.now() / 1000) + 3600, route, verifiedDecision: "APPROVED" };
    await expect(vaultContract.connect(user1).executeHedge(params)).to.be.revertedWithCustomError(vaultContract, "InvalidVault");
  });

  it("5. Attack 5 (Unregistered Instruction ID): Should REVERT executeHedge()", async function () {
    await verifyAttestationForUser(teeSigner);
    const badInstructionId = ethers.keccak256(ethers.toUtf8Bytes("unregistered-instruction"));
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const params = { vaultId, policyCommitment, instructionId: badInstructionId, amountIn: ethers.parseEther("1000"), minimumAmountOut: ethers.parseUnits("850", 6), deadline: Math.floor(Date.now() / 1000) + 3600, route, verifiedDecision: "APPROVED" };
    await expect(vaultContract.connect(user1).executeHedge(params)).to.be.revertedWithCustomError(vaultContract, "InvalidInstructionId");
  });

  it("6. Attack 6 (Insufficient FXRP Balance): Should REVERT executeHedge()", async function () {
    await verifyAttestationForUser(teeSigner);
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const params = { vaultId, policyCommitment, instructionId, amountIn: ethers.parseEther("500000"), minimumAmountOut: ethers.parseUnits("400000", 6), deadline: Math.floor(Date.now() / 1000) + 3600, route, verifiedDecision: "APPROVED" };
    await expect(vaultContract.connect(user1).executeHedge(params)).to.be.revertedWithCustomError(vaultContract, "ExceedsMaxHedgeAmount");
  });

  it("7. Attack 7 (Expired Execution Deadline): Should REVERT executeHedge()", async function () {
    await verifyAttestationForUser(teeSigner);
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const params = { vaultId, policyCommitment, instructionId, amountIn: ethers.parseEther("1000"), minimumAmountOut: ethers.parseUnits("850", 6), deadline: Math.floor(Date.now() / 1000) - 100, route, verifiedDecision: "APPROVED" };
    await expect(vaultContract.connect(user1).executeHedge(params)).to.be.revertedWithCustomError(vaultContract, "PolicyExpired");
  });

  it("8. Attack 8 (Replayed ActionResult Nonce): Should REVERT second submitPolicyEvaluationResult()", async function () {
    await verifyAttestationForUser(teeSigner);
    await expect(verifyAttestationForUser(teeSigner)).to.be.revertedWithCustomError(vaultContract, "InstructionAlreadyProcessed");
  });

  it("9. Attack 9 (Duplicate Execution Attempt): Should REVERT second executeHedge()", async function () {
    await verifyAttestationForUser(teeSigner);
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const params = { vaultId, policyCommitment, instructionId, amountIn: ethers.parseEther("1000"), minimumAmountOut: ethers.parseUnits("850", 6), deadline: Math.floor(Date.now() / 1000) + 3600, route, verifiedDecision: "APPROVED" };
    await vaultContract.connect(user1).executeHedge(params);
    await expect(vaultContract.connect(user1).executeHedge(params)).to.be.revertedWithCustomError(vaultContract, "InstructionAlreadyExecuted");
  });

  it("10. Attack 10 (Unauthorized DEX Router): Should REVERT HedgeExecutor swap", async function () {
    const badRouter = "0x0000000000000000000000000000000000008888";
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    await expect(hedgeExecutor.executeSwap(badRouter, ethers.parseEther("100"), ethers.parseUnits("85", 6), route, vaultContract.target, Math.floor(Date.now() / 1000) + 3600)).to.be.revertedWithCustomError(hedgeExecutor, "RouterNotApproved");
  });

  it("11. Attack 11 (Arbitrary Recipient Wallet): Should REVERT HedgeExecutor swap", async function () {
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    await expect(hedgeExecutor.executeSwap(dexRouterAddress, ethers.parseEther("100"), ethers.parseUnits("85", 6), route, user2.address, Math.floor(Date.now() / 1000) + 3600)).to.be.revertedWithCustomError(hedgeExecutor, "InvalidRecipient");
  });

  it("12. Attack 12 (Tampered Hedge Amount > Approved): Should REVERT executeHedge()", async function () {
    await verifyAttestationForUser(teeSigner);
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const params = { vaultId, policyCommitment, instructionId, amountIn: ethers.parseEther("20000"), minimumAmountOut: ethers.parseUnits("17000", 6), deadline: Math.floor(Date.now() / 1000) + 3600, route, verifiedDecision: "APPROVED" };
    await expect(vaultContract.connect(user1).executeHedge(params)).to.be.revertedWithCustomError(vaultContract, "ExceedsMaxHedgeAmount");
  });

  it("13. Attack 13 (Unauthorized Caller): Should REVERT executeHedge()", async function () {
    await verifyAttestationForUser(teeSigner);
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const params = { vaultId, policyCommitment, instructionId, amountIn: ethers.parseEther("1000"), minimumAmountOut: ethers.parseUnits("850", 6), deadline: Math.floor(Date.now() / 1000) + 3600, route, verifiedDecision: "APPROVED" };
    await expect(vaultContract.connect(user2).executeHedge(params)).to.be.revertedWithCustomError(vaultContract, "UnauthorizedCaller");
  });

  it("14. Attack 14 (Rejected FCC Decision): Should REVERT executeHedge()", async function () {
    await verifyAttestationForUser(teeSigner, "REJECTED");
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const params = { vaultId, policyCommitment, instructionId, amountIn: ethers.parseEther("1000"), minimumAmountOut: ethers.parseUnits("850", 6), deadline: Math.floor(Date.now() / 1000) + 3600, route, verifiedDecision: "APPROVED" };
    await expect(vaultContract.connect(user1).executeHedge(params)).to.be.revertedWithCustomError(vaultContract, "InvalidDecision");
  });

  it("15. Attack 15 (Invalid Route Asset): Should REVERT executeHedge()", async function () {
    await verifyAttestationForUser(teeSigner);
    const badAsset = "0x0000000000000000000000000000000000007777";
    const route = [badAsset, "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const params = { vaultId, policyCommitment, instructionId, amountIn: ethers.parseEther("1000"), minimumAmountOut: ethers.parseUnits("850", 6), deadline: Math.floor(Date.now() / 1000) + 3600, route, verifiedDecision: "APPROVED" };
    await expect(vaultContract.connect(user1).executeHedge(params)).to.be.revertedWithCustomError(vaultContract, "InvalidRoute");
  });
});
