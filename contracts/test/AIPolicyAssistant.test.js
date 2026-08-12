const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield Non-Custodial AI Assistant & User Signer Authorization Tests", function () {
  let deployer, user, unauthorizedAiKey;
  let accessManager, treasuryStorage, vaultManager;
  let vaultAddress;

  beforeEach(async function () {
    [deployer, user, unauthorizedAiKey] = await ethers.getSigners();
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
    await vaultManager.connect(user).registerVault(vaultAddress, "AI Test Vault", "FXRP");
  });

  it("Should ACCEPT AI policy draft ONLY when explicitly signed & submitted by vault owner", async function () {
    const aiDraftPolicy = {
      hedgeRatio: "0.7000",
      triggerThreshold: "5.00",
      maximumProtection: "100000.0",
      deadline: Math.floor(Date.now() / 1000) + 3600,
      nonce: 8001,
      policyVersion: 1
    };

    const canonicalPayloadStr = JSON.stringify({
      vaultAddress: vaultAddress.toLowerCase(),
      asset: "FXRP",
      hedgeRatio: aiDraftPolicy.hedgeRatio,
      triggerThreshold: aiDraftPolicy.triggerThreshold,
      maximumProtection: aiDraftPolicy.maximumProtection,
      deadline: aiDraftPolicy.deadline,
      nonce: aiDraftPolicy.nonce,
      policyVersion: aiDraftPolicy.policyVersion
    });

    const policyHash = ethers.keccak256(ethers.toUtf8Bytes(canonicalPayloadStr));

    // User explicitly approves draft and signs Web3 transaction on Coston2
    await expect(
      vaultManager.connect(user).registerPolicyCommitmentV2(
        vaultAddress, policyHash, aiDraftPolicy.deadline, aiDraftPolicy.nonce, 1, "ipfs://ai-approved-draft"
      )
    ).to.emit(vaultManager, "PolicyCommitmentRegistered");

    const isVerified = await vaultManager.verifyPolicyCommitment(vaultAddress, policyHash);
    expect(isVerified).to.be.true;
  });

  it("Should REJECT AI drafts submitted directly by unauthorized AI keys", async function () {
    const aiDraftHash = ethers.keccak256(ethers.toUtf8Bytes("unapproved-ai-draft"));

    // Unauthorized AI key attempts to submit without user review -> REVERTED
    await expect(
      vaultManager.connect(unauthorizedAiKey).registerPolicyCommitmentV2(
        vaultAddress, aiDraftHash, Math.floor(Date.now() / 1000) + 3600, 8002, 1, "ipfs://ai-unapproved"
      )
    ).to.be.revertedWithCustomError(vaultManager, "UnauthorizedCaller");
  });
});
