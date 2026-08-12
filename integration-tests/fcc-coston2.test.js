const { expect } = require("chai");
const { ethers } = require("../contracts/node_modules/ethers");
const { evaluateHedgePolicy, getExtensionStatus } = require("../extension/src/evaluator");

describe("Real Flare Confidential Compute (FCC) Extension Integration Tests", function () {
  it("Should return valid extension status for GET_EXTENSION_STATUS command", function () {
    const status = getExtensionStatus();

    console.log("--- FCC Extension Status ---");
    console.log(status);

    expect(status.status).to.equal("ACTIVE");
    expect(status.network).to.equal("Flare Coston2 Testnet");
    expect(status.chainId).to.equal(114);
    expect(status.signerAddress).to.be.a("string");
    expect(status.signerAddress).to.match(/^0x[a-fA-F0-9]{40}$/);
  });

  it("Should evaluate confidential policy and produce cryptographically signed ActionResult payload", async function () {
    const vaultAddress = "0x5bb8082987515f40398fb9893d90616b47c04208";
    const policyHash = ethers.keccak256(ethers.toUtf8Bytes("canonical-xrpshield-policy-v1"));

    const result = await evaluateHedgePolicy(
      vaultAddress,
      policyHash,
      "1.0225",
      "100000",
      10,
      50000
    );

    console.log("--- FCC Signed ActionResult Payload ---");
    console.log(result);

    expect(result.success).to.be.true;
    expect(result.status).to.equal("COMPLIANT");
    expect(result.policyHash).to.equal(policyHash);
    expect(result.attestationHash).to.be.a("string");
    expect(result.signature).to.be.a("string");

    // Verify ECDSA signature of ActionResult
    const msgHash = ethers.solidityPackedKeccak256(
      ['address', 'bytes32', 'string', 'bytes32', 'uint256'],
      [vaultAddress, policyHash, result.status, result.attestationHash, result.evaluatedAt]
    );

    const recoveredAddress = ethers.verifyMessage(ethers.getBytes(msgHash), result.signature);
    console.log("Recovered TEE Signer Address:", recoveredAddress);
    expect(recoveredAddress).to.equal(result.signerAddress);
  });
});
