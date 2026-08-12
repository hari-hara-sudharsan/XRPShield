const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield Real On-Chain Hedge Execution Unit Tests", function () {
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
    const tx = await vaultContract.connect(user1).createVault(user1.address, "Hedge Execution Vault");
    const receipt = await tx.wait();
    const event = receipt.logs.find(log => log.fragment && log.fragment.name === "VaultCreated");
    vaultId = event.args.vaultId;

    await fxrpToken.mint(user1.address, ethers.parseEther("100000"));
    await fxrpToken.connect(user1).approve(await vaultContract.getAddress(), ethers.parseEther("100000"));
    await vaultContract.connect(user1).depositFXRP(vaultId, ethers.parseEther("100000"));

    policyCommitment = ethers.keccak256(ethers.toUtf8Bytes("canonical-fcc-policy-v1"));

    const reqTx = await vaultContract.connect(user1).requestPolicyEvaluation(
      vaultId,
      policyCommitment,
      ethers.parseUnits("0.85", 8),
      Math.floor(Date.now() / 1000) + 3600
    );
    const reqReceipt = await reqTx.wait();
    const reqEvent = reqReceipt.logs.find(log => log.fragment && log.fragment.name === "PolicyEvaluationRequested");
    instructionId = reqEvent.args.instructionId;

    // Verify Attestation
    const timestamp = Math.floor(Date.now() / 1000);
    const deadline = timestamp + 3600;
    const attestationHash = ethers.keccak256(
      ethers.AbiCoder.defaultAbiCoder().encode(
        ["address", "bytes32", "string", "uint256", "uint256"],
        [user1.address, policyCommitment, "APPROVED", 301, timestamp]
      )
    );

    const domain = { name: "XRPShield FCC Extension", version: "1", chainId: chainId, verifyingContract: adapter.target };
    const types = { ActionResult: [{ name: "vaultAddress", type: "address" }, { name: "policyHash", type: "bytes32" }, { name: "status", type: "string" }, { name: "attestationHash", type: "bytes32" }, { name: "nonce", type: "uint256" }, { name: "timestamp", type: "uint256" }, { name: "deadline", type: "uint256" }] };
    const value = { vaultAddress: user1.address, policyHash: policyCommitment, status: "APPROVED", attestationHash: attestationHash, nonce: 301, timestamp: timestamp, deadline: deadline };

    const signature = await teeSigner.signTypedData(domain, types, value);
    const actionResult = { success: true, status: "APPROVED", rationale: "Triggered", policyHash: policyCommitment, attestationHash: attestationHash, nonce: 301, timestamp: timestamp, deadline: deadline, signature: signature };

    await vaultContract.submitPolicyEvaluationResult(instructionId, vaultId, policyCommitment, "APPROVED", ethers.parseUnits("10000", 18), actionResult);
  });

  it("1. Should successfully execute on-chain hedge swap and deposit USDT0 to vault custody", async function () {
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const deadline = Math.floor(Date.now() / 1000) + 3600;
    const amountIn = ethers.parseEther("10000");
    const minAmountOut = ethers.parseUnits("8500", 6);

    const params = {
      vaultId: vaultId,
      policyCommitment: policyCommitment,
      instructionId: instructionId,
      amountIn: amountIn,
      minimumAmountOut: minAmountOut,
      deadline: deadline,
      route: route,
      verifiedDecision: "APPROVED"
    };

    await expect(
      vaultContract.connect(user1).executeHedge(params)
    ).to.emit(vaultContract, "HedgeExecuted")
      .withArgs(vaultId, policyCommitment, instructionId, amountIn, minAmountOut, (r) => r !== ethers.ZeroAddress, (ts) => ts > 0);

    const vault = await vaultContract.vaults(vaultId);
    expect(vault.currentBalance).to.equal(ethers.parseEther("90000"));
    expect(vault.usdt0Balance).to.equal(minAmountOut);
  });

  it("2. Should revert if instruction has already been executed", async function () {
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const deadline = Math.floor(Date.now() / 1000) + 3600;
    const amountIn = ethers.parseEther("10000");
    const minAmountOut = ethers.parseUnits("8500", 6);

    const params = {
      vaultId: vaultId,
      policyCommitment: policyCommitment,
      instructionId: instructionId,
      amountIn: amountIn,
      minimumAmountOut: minAmountOut,
      deadline: deadline,
      route: route,
      verifiedDecision: "APPROVED"
    };

    await vaultContract.connect(user1).executeHedge(params);

    await expect(
      vaultContract.connect(user1).executeHedge(params)
    ).to.be.revertedWithCustomError(vaultContract, "InstructionAlreadyExecuted");
  });

  it("3. Should revert if caller is unauthorized", async function () {
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const deadline = Math.floor(Date.now() / 1000) + 3600;

    const params = {
      vaultId: vaultId,
      policyCommitment: policyCommitment,
      instructionId: instructionId,
      amountIn: ethers.parseEther("10000"),
      minimumAmountOut: ethers.parseUnits("8500", 6),
      deadline: deadline,
      route: route,
      verifiedDecision: "APPROVED"
    };

    await expect(
      vaultContract.connect(user2).executeHedge(params)
    ).to.be.revertedWithCustomError(vaultContract, "UnauthorizedCaller");
  });

  it("4. Should revert if verified decision is not APPROVED", async function () {
    const route = [await fxrpToken.getAddress(), "0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780"];
    const deadline = Math.floor(Date.now() / 1000) + 3600;

    const params = {
      vaultId: vaultId,
      policyCommitment: policyCommitment,
      instructionId: instructionId,
      amountIn: ethers.parseEther("10000"),
      minimumAmountOut: ethers.parseUnits("8500", 6),
      deadline: deadline,
      route: route,
      verifiedDecision: "REJECTED"
    };

    await expect(
      vaultContract.connect(user1).executeHedge(params)
    ).to.be.revertedWithCustomError(vaultContract, "InvalidDecision");
  });
});
