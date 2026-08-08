require("@nomicfoundation/hardhat-toolbox");
require("dotenv").config({ path: "../.env" });

const FLARE_RPC_URL = process.env.FLARE_RPC_URL || "https://coston2-api.flare.network/ext/C/rpc";
const PRIVATE_KEY = process.env.PRIVATE_KEY && process.env.PRIVATE_KEY !== "0x0000000000000000000000000000000000000000000000000000000000000000"
  ? [process.env.PRIVATE_KEY]
  : [];

/** @type import('hardhat/config').HardhatUserConfig */
module.exports = {
  solidity: {
    version: "0.8.24",
    settings: {
      optimizer: {
        enabled: true,
        runs: 200,
      },
    },
  },
  networks: {
    hardhat: {
      chainId: 31337,
    },
    coston2: {
      url: FLARE_RPC_URL,
      chainId: 114,
      accounts: PRIVATE_KEY,
    },
    flare: {
      url: "https://flare-api.flare.network/ext/C/rpc",
      chainId: 14,
      accounts: PRIVATE_KEY,
    },
  },
  paths: {
    sources: "./contracts",
    tests: "./test",
    cache: "./cache",
    artifacts: "./artifacts",
  },
  mocha: {
    timeout: 40000,
  },
};
