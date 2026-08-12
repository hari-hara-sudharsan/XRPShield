const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield Flare ActionResult EIP-712 Signature Verification Tests", function () {
  let adapter;
  let owner;
  let teeSigner;
  let vaultOwner;
  let vaultAddress;
  let chainId;

  beforeEach(async function () {
    [owner, teeSigner, vaultOwner] = await ethers.getSigners();
    vaultAddress = vaultOwner.address;

    const FCCExtensionAdapter = await ethers.getContractFactory("FCCExtensionAdapter");
    adapter = await FCCExtensionAdapter.deploy(teeSigner.address);
    await adapter.waitForDeployment();

    const net = await ethers.provider.getNetwork();
    chainId = Number(net.chainId);
  });

  it("1. Should successfully verify authentic EIP-712 TEE ActionResult signature on-chain", async function () {
    const policyHash = ethers.keccak256(ethers.toUtf8Bytes("canonical-policy-v1"));
    const status = "APPROVED";
    const nonce = 101;
    const timestamp = Math.floor(Date.now() / 1000);
    const deadline = timestamp + 3600;

    const attestationHash = ethers.keccak256(
      ethers.AbiCoder.defaultAbiCoder().encode(
        ["address", "bytes32", "string", "uint256", "uint256"],
        [vaultAddress, policyHash, status, nonce, timestamp]
      )
    );

    const domain = {
      name: "XRPShield FCC Extension",
      version: "1",
      chainId: chainId,
      verifyingContract: await adapter.getAddress()
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
      vaultAddress: vaultAddress,
      policyHash: policyHash,
      status: status,
      attestationHash: attestationHash,
      nonce: nonce,
      timestamp: timestamp,
      deadline: deadline
    };

    const signature = await teeSigner.signTypedData(domain, types, value);

    const actionResult = {
      success: true,
      status: status,
      rationale: "Policy triggered by FTSOv2 price drop",
      policyHash: policyHash,
      attestationHash: attestationHash,
      nonce: nonce,
      timestamp: timestamp,
      deadline: deadline,
      signature: signature
    };

    const verified = await adapter.verifyAndRecordAttestation.staticCall(vaultAddress, actionResult);
    expect(verified).to.be.true;

    await expect(adapter.verifyAndRecordAttestation(vaultAddress, actionResult))
      .to.emit(adapter, "ActionResultVerified")
      .withArgs(vaultAddress, policyHash, status, true);

    expect(await adapter.verifiedAttestations(attestationHash)).to.be.true;
    expect(await adapter.vaultNonces(vaultAddress)).to.equal(nonce);
  });

  it("2. Should reject ActionResult with invalid signer signature", async function () {
    const policyHash = ethers.keccak256(ethers.toUtf8Bytes("canonical-policy-v1"));
    const status = "APPROVED";
    const nonce = 102;
    const timestamp = Math.floor(Date.now() / 1000);
    const deadline = timestamp + 3600;

    const attestationHash = ethers.keccak256(ethers.toUtf8Bytes("attestation-2"));

    const domain = {
      name: "XRPShield FCC Extension",
      version: "1",
      chainId: chainId,
      verifyingContract: await adapter.getAddress()
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
      vaultAddress: vaultAddress,
      policyHash: policyHash,
      status: status,
      attestationHash: attestationHash,
      nonce: nonce,
      timestamp: timestamp,
      deadline: deadline
    };

    // Signed by unauthorized key (owner instead of teeSigner)
    const invalidSignature = await owner.signTypedData(domain, types, value);

    const actionResult = {
      success: true,
      status: status,
      rationale: "Fake signature test",
      policyHash: policyHash,
      attestationHash: attestationHash,
      nonce: nonce,
      timestamp: timestamp,
      deadline: deadline,
      signature: invalidSignature
    };

    const verified = await adapter.verifyAndRecordAttestation.staticCall(vaultAddress, actionResult);
    expect(verified).to.be.false;
  });

  it("3. Should reject replayed ActionResult nonce", async function () {
    const policyHash = ethers.keccak256(ethers.toUtf8Bytes("canonical-policy-v1"));
    const status = "APPROVED";
    const nonce = 103;
    const timestamp = Math.floor(Date.now() / 1000);
    const deadline = timestamp + 3600;

    const attestationHash = ethers.keccak256(ethers.toUtf8Bytes("attestation-3"));

    const domain = {
      name: "XRPShield FCC Extension",
      version: "1",
      chainId: chainId,
      verifyingContract: await adapter.getAddress()
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
      vaultAddress: vaultAddress,
      policyHash: policyHash,
      status: status,
      attestationHash: attestationHash,
      nonce: nonce,
      timestamp: timestamp,
      deadline: deadline
    };

    const signature = await teeSigner.signTypedData(domain, types, value);

    const actionResult = {
      success: true,
      status: status,
      rationale: "Replay test",
      policyHash: policyHash,
      attestationHash: attestationHash,
      nonce: nonce,
      timestamp: timestamp,
      deadline: deadline,
      signature: signature
    };

    // First submission succeeds
    await adapter.verifyAndRecordAttestation(vaultAddress, actionResult);

    // Second submission with same nonce fails
    const replayed = await adapter.verifyAndRecordAttestation.staticCall(vaultAddress, actionResult);
    expect(replayed).to.be.false;
  });
});
