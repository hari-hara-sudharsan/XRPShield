const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield On-Chain EIP-712 Attestation Verification & Rejection Tests", function () {
  let deployer, extensionSigner, user;
  let adapter;
  let vaultAddress;

  beforeEach(async function () {
    [deployer, extensionSigner, user] = await ethers.getSigners();
    vaultAddress = user.address;

    const FCCExtensionAdapter = await ethers.getContractFactory("FCCExtensionAdapter");
    adapter = await FCCExtensionAdapter.deploy(extensionSigner.address);
    await adapter.waitForDeployment();
  });

  it("Should ACCEPT a valid EIP-712 signed ActionResult payload from extension TEE signer", async function () {
    const policyHash = ethers.keccak256(ethers.toUtf8Bytes("canonical-xrpshield-policy-v1"));
    const attestationHash = ethers.keccak256(ethers.toUtf8Bytes("attestation-proof-v1"));
    const status = "COMPLIANT";
    const nonce = 1;
    const timestamp = Math.floor(Date.now() / 1000);
    const deadline = timestamp + 3600;

    const network = await ethers.provider.getNetwork();
    const domain = {
      name: "XRPShield FCC Extension",
      version: "1",
      chainId: network.chainId,
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

    const signature = await extensionSigner.signTypedData(domain, types, value);

    const actionResultStruct = {
      success: true,
      status: status,
      rationale: "Confidential TEE policy evaluation passed",
      policyHash: policyHash,
      attestationHash: attestationHash,
      nonce: nonce,
      timestamp: timestamp,
      deadline: deadline,
      signature: signature
    };

    // Verify view method
    const isVerified = await adapter.verifyAttestationView(vaultAddress, actionResultStruct);
    expect(isVerified).to.be.true;

    // Execute state-modifying verification
    await expect(adapter.verifyAndRecordAttestation(vaultAddress, actionResultStruct))
      .to.emit(adapter, "ActionResultVerified")
      .withArgs(vaultAddress, policyHash, status, true);

    const isRecorded = await adapter.verifiedAttestations(attestationHash);
    expect(isRecorded).to.be.true;
  });

  it("Should REJECT forged signatures from unauthorized signers", async function () {
    const policyHash = ethers.keccak256(ethers.toUtf8Bytes("canonical-xrpshield-policy-v1"));
    const attestationHash = ethers.keccak256(ethers.toUtf8Bytes("attestation-proof-forged"));
    const status = "COMPLIANT";
    const nonce = 2;
    const timestamp = Math.floor(Date.now() / 1000);
    const deadline = timestamp + 3600;

    const domain = {
      name: "XRPShield FCC Extension",
      version: "1",
      chainId: 114,
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

    // Sign with user (unauthorized) instead of extensionSigner
    const forgedSignature = await user.signTypedData(domain, types, value);

    const actionResultStruct = {
      success: true,
      status: status,
      rationale: "Forged TEE payload",
      policyHash: policyHash,
      attestationHash: attestationHash,
      nonce: nonce,
      timestamp: timestamp,
      deadline: deadline,
      signature: forgedSignature
    };

    const isVerified = await adapter.verifyAttestationView(vaultAddress, actionResultStruct);
    expect(isVerified).to.be.false;
  });

  it("Should REJECT expired attestation deadlines", async function () {
    const policyHash = ethers.keccak256(ethers.toUtf8Bytes("canonical-policy"));
    const attestationHash = ethers.keccak256(ethers.toUtf8Bytes("attestation-expired"));
    const expiredDeadline = Math.floor(Date.now() / 1000) - 600; // Expired 10 mins ago

    const actionResultStruct = {
      success: true,
      status: "COMPLIANT",
      rationale: "Expired",
      policyHash: policyHash,
      attestationHash: attestationHash,
      nonce: 3,
      timestamp: expiredDeadline - 10,
      deadline: expiredDeadline,
      signature: "0x00"
    };

    const isVerified = await adapter.verifyAttestationView(vaultAddress, actionResultStruct);
    expect(isVerified).to.be.false;
  });

  it("Should REJECT replayed nonces", async function () {
    const policyHash = ethers.keccak256(ethers.toUtf8Bytes("canonical-policy"));
    const attestationHash = ethers.keccak256(ethers.toUtf8Bytes("attestation-replay"));
    const timestamp = Math.floor(Date.now() / 1000);
    const deadline = timestamp + 3600;

    const domain = {
      name: "XRPShield FCC Extension",
      version: "1",
      chainId: 114,
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
      status: "COMPLIANT",
      attestationHash: attestationHash,
      nonce: 10,
      timestamp: timestamp,
      deadline: deadline
    };

    const signature = await extensionSigner.signTypedData(domain, types, value);

    const actionResultStruct = {
      success: true,
      status: "COMPLIANT",
      rationale: "Replay test",
      policyHash: policyHash,
      attestationHash: attestationHash,
      nonce: 10,
      timestamp: timestamp,
      deadline: deadline,
      signature: signature
    };

    // First recording succeeds
    await adapter.verifyAndRecordAttestation(vaultAddress, actionResultStruct);

    // Second recording with same nonce (10) should fail
    const isReplayed = await adapter.verifyAttestationView(vaultAddress, actionResultStruct);
    expect(isReplayed).to.be.false;
  });
});
