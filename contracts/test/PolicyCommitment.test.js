const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield Canonical Policy Commitment & Anti-Replay Coston2 Tests", function () {
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
    await vaultManager.connect(user).registerVault(vaultAddress, "Test Vault", "FXRP");
  });

  it("Should register canonical keccak256 policy commitment on-chain", async function () {
    const canonicalPayload = JSON.stringify({
      vaultAddress: vaultAddress,
      asset: "FXRP",
      hedgeRatio: "1.0000",
      triggerThreshold: "10.0",
      maximumProtection: "100000.0",
      deadline: Math.floor(Date.now() / 1000) + 3600,
      nonce: 101,
      policyVersion: 1
    });

    const policyHash = ethers.keccak256(ethers.toUtf8Bytes(canonicalPayload));
    const deadline = Math.floor(Date.now() / 1000) + 3600;
    const nonce = 101;
    const version = 1;

    await expect(
      vaultManager.connect(user).registerPolicyCommitmentV2(
        vaultAddress, policyHash, deadline, nonce, version, "ipfs://test-policy-metadata"
      )
    ).to.emit(vaultManager, "PolicyCommitmentRegistered");

    const isVerified = await vaultManager.verifyPolicyCommitment(vaultAddress, policyHash);
    expect(isVerified).to.be.true;

    const dummyHash = ethers.keccak256(ethers.toUtf8Bytes("fake-payload"));
    const isDummyVerified = await vaultManager.verifyPolicyCommitment(vaultAddress, dummyHash);
    expect(isDummyVerified).to.be.false;
  });

  it("Should enforce anti-replay protection for nonces and versions", async function () {
    const policyHash1 = ethers.keccak256(ethers.toUtf8Bytes("policy-v1"));
    const deadline = Math.floor(Date.now() / 1000) + 3600;

    // First commitment (nonce 1, version 1)
    await vaultManager.connect(user).registerPolicyCommitmentV2(
      vaultAddress, policyHash1, deadline, 1, 1, "ipfs://v1"
    );

    // Replay attack with same nonce (1) should revert
    const policyHash2 = ethers.keccak256(ethers.toUtf8Bytes("policy-v2-replay"));
    await expect(
      vaultManager.connect(user).registerPolicyCommitmentV2(
        vaultAddress, policyHash2, deadline, 1, 2, "ipfs://v2"
      )
    ).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");

    // Replay attack with same version (1) should revert
    await expect(
      vaultManager.connect(user).registerPolicyCommitmentV2(
        vaultAddress, policyHash2, deadline, 2, 1, "ipfs://v2"
      )
    ).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");

    // Valid update (nonce 2, version 2) should succeed
    await expect(
      vaultManager.connect(user).registerPolicyCommitmentV2(
        vaultAddress, policyHash2, deadline, 2, 2, "ipfs://v2"
      )
    ).to.emit(vaultManager, "PolicyCommitmentRegistered");

    const isV2Verified = await vaultManager.verifyPolicyCommitment(vaultAddress, policyHash2);
    expect(isV2Verified).to.be.true;
  });
});
