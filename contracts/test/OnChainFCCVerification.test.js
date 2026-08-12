const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield Real On-Chain FCC Result Verification Unit Tests", function () {
  let vaultContract;
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

    await vaultContract.setFccAdapterAddress(await adapter.getAddress());

    const net = await ethers.provider.getNetwork();
    chainId = Number(net.chainId);

    const tx = await vaultContract.connect(user1).createVault(user1.address, "FCC Vault");
    const receipt = await tx.wait();
    const event = receipt.logs.find(log => log.fragment && log.fragment.name === "VaultCreated");
    vaultId = event.args.vaultId;

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
  });

  function createSignedActionResult(status, nonce, timestamp, deadline, signer) {
    const attestationHash = ethers.keccak256(
      ethers.AbiCoder.defaultAbiCoder().encode(
        ["address", "bytes32", "string", "uint256", "uint256"],
        [user1.address, policyCommitment, status, nonce, timestamp]
      )
    );

    const domain = {
      name: "XRPShield FCC Extension",
      version: "1",
      chainId: chainId,
      verifyingContract: adapter.target
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
      vaultAddress: user1.address,
      policyHash: policyCommitment,
      status: status,
      attestationHash: attestationHash,
      nonce: nonce,
      timestamp: timestamp,
      deadline: deadline
    };

    return signer.signTypedData(domain, types, value).then(signature => {
      return {
        success: true,
        status: status,
        rationale: "FTSOv2 Triggered",
        policyHash: policyCommitment,
        attestationHash: attestationHash,
        nonce: nonce,
        timestamp: timestamp,
        deadline: deadline,
        signature: signature
      };
    });
  }

  it("1. Should successfully verify genuine TEE ActionResult and update instruction status to VERIFIED", async function () {
    const timestamp = Math.floor(Date.now() / 1000);
    const deadline = timestamp + 3600;
    const actionResult = await createSignedActionResult("APPROVED", 201, timestamp, deadline, teeSigner);

    await expect(
      vaultContract.submitPolicyEvaluationResult(
        instructionId,
        vaultId,
        policyCommitment,
        "APPROVED",
        ethers.parseUnits("10000", 18),
        actionResult
      )
    ).to.emit(vaultContract, "PolicyEvaluationVerified")
      .withArgs(vaultId, policyCommitment, instructionId, "APPROVED", ethers.parseUnits("10000", 18));

    const record = await vaultContract.instructions(instructionId);
    expect(record.status).to.equal("VERIFIED");
    expect(await vaultContract.processedInstructionIds(instructionId)).to.be.true;
  });

  it("2. Should revert on replayed instruction submission", async function () {
    const timestamp = Math.floor(Date.now() / 1000);
    const deadline = timestamp + 3600;
    const actionResult = await createSignedActionResult("APPROVED", 202, timestamp, deadline, teeSigner);

    await vaultContract.submitPolicyEvaluationResult(
      instructionId,
      vaultId,
      policyCommitment,
      "APPROVED",
      ethers.parseUnits("10000", 18),
      actionResult
    );

    await expect(
      vaultContract.submitPolicyEvaluationResult(
        instructionId,
        vaultId,
        policyCommitment,
        "APPROVED",
        ethers.parseUnits("10000", 18),
        actionResult
      )
    ).to.be.revertedWithCustomError(vaultContract, "InstructionAlreadyProcessed");
  });

  it("3. Should revert on invalid TEE signature", async function () {
    const timestamp = Math.floor(Date.now() / 1000);
    const deadline = timestamp + 3600;
    // Signed by unauthorized user2 instead of teeSigner
    const invalidActionResult = await createSignedActionResult("APPROVED", 203, timestamp, deadline, user2);

    await expect(
      vaultContract.submitPolicyEvaluationResult(
        instructionId,
        vaultId,
        policyCommitment,
        "APPROVED",
        ethers.parseUnits("10000", 18),
        invalidActionResult
      )
    ).to.be.revertedWithCustomError(vaultContract, "AttestationVerificationFailed");
  });

  it("4. Should revert on policy commitment mismatch", async function () {
    const timestamp = Math.floor(Date.now() / 1000);
    const deadline = timestamp + 3600;
    const actionResult = await createSignedActionResult("APPROVED", 204, timestamp, deadline, teeSigner);
    const wrongCommitment = ethers.keccak256(ethers.toUtf8Bytes("wrong-policy"));

    await expect(
      vaultContract.submitPolicyEvaluationResult(
        instructionId,
        vaultId,
        wrongCommitment,
        "APPROVED",
        ethers.parseUnits("10000", 18),
        actionResult
      )
    ).to.be.revertedWithCustomError(vaultContract, "InvalidPolicyCommitment");
  });
});
