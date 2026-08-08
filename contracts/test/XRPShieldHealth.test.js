const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShieldHealth Contract Suite", function () {
  let healthContract;
  let owner;

  beforeEach(async function () {
    [owner] = await ethers.getSigners();
    const XRPShieldHealth = await ethers.getContractFactory("XRPShieldHealth");
    healthContract = await XRPShieldHealth.deploy();
    await healthContract.waitForDeployment();
  });

  it("Should deploy cleanly and report correct system name and version", async function () {
    const info = await healthContract.getSystemInfo();
    expect(info.name).to.equal("XRPShield");
    expect(info.ver).to.equal("1.0.0");
    expect(info.contractOwner).to.equal(owner.address);
  });

  it("Should execute verifyStatus and emit SystemStatusVerified event", async function () {
    await expect(healthContract.verifyStatus())
      .to.emit(healthContract, "SystemStatusVerified");
  });
});
