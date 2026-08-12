const { expect } = require("chai");
const { ethers } = require("../contracts/node_modules/ethers");
const { evaluatePrivateHedgePolicy } = require("../../extension/src/evaluator");

describe("XRPShield Private Policy Engine TEE Evaluation Test Suite", function () {
  const vaultAddress = "0x5bb8082987515f40398fb9893d90616b47c04208";
  const now = Math.floor(Date.now() / 1000);

  const validPolicy = {
    hedgeRatio: "1.0000",
    triggerThreshold: "10.0",
    maximumProtection: "100000.0",
    deadline: now + 3600,
    nonce: 1001,
    policyVersion: 1
  };

  const canonicalPayloadStr = JSON.stringify({
    vaultAddress: vaultAddress.toLowerCase(),
    asset: "FXRP",
    hedgeRatio: validPolicy.hedgeRatio,
    triggerThreshold: validPolicy.triggerThreshold,
    maximumProtection: validPolicy.maximumProtection,
    deadline: validPolicy.deadline,
    nonce: validPolicy.nonce,
    policyVersion: validPolicy.policyVersion
  });

  const validCommittedHash = ethers.keccak256(ethers.toUtf8Bytes(canonicalPayloadStr));

  it("1. APPROVED Case: FTSOv2 price drop >= trigger threshold (15% drop >= 10%)", async function () {
    const result = await evaluatePrivateHedgePolicy({
      vaultAddress: vaultAddress,
      committedPolicyHash: validCommittedHash,
      policy: validPolicy,
      currentPrice: "0.9500", // $0.95 vs $1.15 = 17.39% drop
      referencePrice: "1.1500",
      vaultBalance: "100000",
      vaultStatus: "ACTIVE"
    });

    console.log("--- Approved Case Result ---");
    console.log(result);

    expect(result.success).to.be.true;
    expect(result.decision).to.equal("APPROVED");
    expect(result.approvedHedgeAmount).to.equal(100000);
    expect(result.signature).to.be.a("string");
  });

  it("2. NO_ACTION Case: FTSOv2 price drop < trigger threshold (5% drop < 10%)", async function () {
    const result = await evaluatePrivateHedgePolicy({
      vaultAddress: vaultAddress,
      committedPolicyHash: validCommittedHash,
      policy: validPolicy,
      currentPrice: "1.1000", // $1.10 vs $1.15 = 4.34% drop
      referencePrice: "1.1500",
      vaultBalance: "100000",
      vaultStatus: "ACTIVE"
    });

    console.log("--- No Action Case Result ---");
    console.log(result);

    expect(result.success).to.be.true;
    expect(result.decision).to.equal("NO_ACTION");
    expect(result.approvedHedgeAmount).to.equal(0);
  });

  it("3. EXPIRED POLICY Case: Current time > deadline", async function () {
    const expiredPolicy = { ...validPolicy, deadline: now - 600 }; // Expired 10 mins ago

    const result = await evaluatePrivateHedgePolicy({
      vaultAddress: vaultAddress,
      committedPolicyHash: validCommittedHash,
      policy: expiredPolicy,
      currentPrice: "0.9000",
      referencePrice: "1.1500",
      vaultBalance: "100000",
      vaultStatus: "ACTIVE"
    });

    console.log("--- Expired Policy Result ---");
    console.log(result);

    expect(result.success).to.be.false;
    expect(result.error).to.equal("POLICY_EXPIRED");
  });

  it("4. WRONG COMMITMENT Case: Policy hash does not match committed hash", async function () {
    const wrongCommittedHash = ethers.keccak256(ethers.toUtf8Bytes("wrong-policy-payload"));

    const result = await evaluatePrivateHedgePolicy({
      vaultAddress: vaultAddress,
      committedPolicyHash: wrongCommittedHash,
      policy: validPolicy,
      currentPrice: "0.9000",
      referencePrice: "1.1500",
      vaultBalance: "100000",
      vaultStatus: "ACTIVE"
    });

    console.log("--- Wrong Commitment Result ---");
    console.log(result);

    expect(result.success).to.be.false;
    expect(result.error).to.equal("POLICY_COMMITMENT_MISMATCH");
  });

  it("5. INSUFFICIENT BALANCE Case: Vault balance is zero", async function () {
    const result = await evaluatePrivateHedgePolicy({
      vaultAddress: vaultAddress,
      committedPolicyHash: validCommittedHash,
      policy: validPolicy,
      currentPrice: "0.9000",
      referencePrice: "1.1500",
      vaultBalance: "0",
      vaultStatus: "ACTIVE"
    });

    console.log("--- Insufficient Balance Result ---");
    console.log(result);

    expect(result.success).to.be.false;
    expect(result.error).to.equal("INSUFFICIENT_BALANCE");
  });
});
