const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("Flare FTSOv2 XRP/USD Real Coston2 Oracle Integration Tests", function () {
  const FTSOV2_ADDRESS = "0xC4e9c78EA53db782E28f28Fdf80BaF59336B304d";
  const XRP_USD_FEED_ID = "0x015852502f55534400000000000000000000000000";

  let ftsoV2;

  before(async function () {
    const abi = [
      "function getFeedById(bytes21 _feedId) external view returns (uint256 _value, int8 _decimals, uint64 _timestamp)"
    ];
    ftsoV2 = await ethers.getContractAt(abi, FTSOV2_ADDRESS);
  });

  it("Should query real live XRP/USD feed from FTSOv2 on Coston2 Testnet", async function () {
    const [value, decimals, timestamp] = await ftsoV2.getFeedById(XRP_USD_FEED_ID);

    console.log("--- FTSOv2 On-Chain Response ---");
    console.log("Raw Value:", value.toString());
    console.log("Decimals:", decimals.toString());
    console.log("Timestamp:", timestamp.toString());

    expect(value).to.be.gt(0);
    expect(decimals).to.be.gte(0);
    expect(timestamp).to.be.gt(0);

    const formattedPrice = Number(value) / Math.pow(10, Number(decimals));
    console.log(`Live Coston2 XRP/USD Price: $${formattedPrice.toFixed(4)}`);
    expect(formattedPrice).to.be.gt(0);
  });

  it("Should verify VaultManager getLatestXRPUSDPrice integration", async function () {
    const [deployer] = await ethers.getSigners();

    const AccessManager = await ethers.getContractFactory("AccessManager");
    const accessManager = await AccessManager.deploy(deployer.address);

    const TreasuryStorage = await ethers.getContractFactory("TreasuryStorage");
    const treasuryStorage = await TreasuryStorage.deploy();

    const VaultManager = await ethers.getContractFactory("VaultManager");
    const vaultManager = await VaultManager.deploy(
      await accessManager.getAddress(),
      await treasuryStorage.getAddress()
    );

    const [value, decimals, timestamp] = await vaultManager.getLatestXRPUSDPrice();
    expect(value).to.be.gt(0);
    expect(timestamp).to.be.gt(0);
  });
});
