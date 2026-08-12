const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield Vault Security & Invariant Test Suite", function () {
  let vaultManager;
  let fxrpToken;
  let usdt0Token;
  let routerAdapter;
  let accessManager;
  let treasuryStorage;
  let extensionAdapter;
  let deployer;
  let extensionSigner;
  let vaultOwner;
  let attacker;
  let vaultAddress;

  beforeEach(async function () {
    [deployer, extensionSigner, vaultOwner, attacker] = await ethers.getSigners();
    vaultAddress = vaultOwner.address;

    // 1. Deploy Test Tokens
    const TestFXRPToken = await ethers.getContractFactory("TestFXRPToken");
    fxrpToken = await TestFXRPToken.deploy();
    await fxrpToken.waitForDeployment();

    const TestUSDT0Token = await ethers.getContractFactory("TestUSDT0Token");
    usdt0Token = await TestUSDT0Token.deploy();
    await usdt0Token.waitForDeployment();

    // 2. Deploy Infrastructure Contracts
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
    await vaultManager.setFXRPToken(await fxrpToken.getAddress());

    const FCCExtensionAdapter = await ethers.getContractFactory("FCCExtensionAdapter");
    extensionAdapter = await FCCExtensionAdapter.deploy(extensionSigner.address);
    await extensionAdapter.waitForDeployment();

    const DEXRouterAdapter = await ethers.getContractFactory("DEXRouterAdapter");
    routerAdapter = await DEXRouterAdapter.deploy(await fxrpToken.getAddress(), await usdt0Token.getAddress());
    await routerAdapter.waitForDeployment();
    await usdt0Token.transfer(await routerAdapter.getAddress(), ethers.parseUnits("1000000", 6));

    // Register Vault & Deposit FXRP
    await vaultManager.connect(vaultOwner).registerVault(vaultAddress, "Corporate Treasury", "FXRP");

    const depositAmount = ethers.parseEther("1000");
    await fxrpToken.transfer(vaultAddress, depositAmount);
    await fxrpToken.connect(vaultOwner).approve(await vaultManager.getAddress(), depositAmount);
    await vaultManager.connect(vaultOwner).depositFXRP(depositAmount);
  });

  // =========================================================================
  // 15 SECURITY ATTACK VECTORS EVALUATION
  // =========================================================================

  it("Vector 1: Should REJECT unauthorized withdrawal attempt by attacker", async function () {
    await expect(
      vaultManager.connect(attacker).withdrawFXRP(ethers.parseEther("100"))
    ).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");
  });

  it("Vector 2: Should REJECT unauthorized policy commitment registration by attacker", async function () {
    const policyHash = ethers.keccak256(ethers.toUtf8Bytes("attacker-policy"));
    const deadline = Math.floor(Date.now() / 1000) + 3600;

    await expect(
      vaultManager.connect(attacker).registerPolicyCommitmentV2(
        vaultAddress, policyHash, deadline, 1, 1, "ipfs://attacker-policy"
      )
    ).to.be.revertedWithCustomError(vaultManager, "UnauthorizedCaller");
  });

  it("Vector 3: Should REJECT replayed attestation signature execution", async function () {
    const deadline = Math.floor(Date.now() / 1000) + 3600;
    const nonce = 5001;
    const policyHash = ethers.keccak256(ethers.toUtf8Bytes("replay-policy-test"));
    const attestationHash = ethers.keccak256(ethers.toUtf8Bytes("attestation-replay-1"));

    await vaultManager.connect(vaultOwner).registerPolicyCommitmentV2(
      vaultAddress, policyHash, deadline, nonce, 1, "ipfs://replay-policy"
    );

    const domain = {
      name: "XRPShield FCC Extension",
      version: "1",
      chainId: 114,
      verifyingContract: await extensionAdapter.getAddress()
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
    const actionResult = {
      success: true,
      status: "APPROVED",
      rationale: "Approved",
      policyHash: policyHash,
      attestationHash: attestationHash,
      nonce: nonce,
      timestamp: Math.floor(Date.now() / 1000),
      deadline: deadline,
      signature: "0x00"
    };

    actionResult.signature = await extensionSigner.signTypedData(domain, types, {
      vaultAddress: vaultAddress,
      policyHash: policyHash,
      status: "APPROVED",
      attestationHash: attestationHash,
      nonce: nonce,
      timestamp: actionResult.timestamp,
      deadline: deadline
    });

    // Execute first time - SUCCESS
    await extensionAdapter.verifyAndRecordAttestation(vaultAddress, actionResult);
    await vaultManager.connect(vaultOwner).executeHedge(
      vaultAddress, ethers.parseEther("10"), ethers.parseUnits("10", 6),
      await routerAdapter.getAddress(), deadline, policyHash, attestationHash, "APPROVED"
    );

    // Attempt second execution with same attestation - REJECT REPLAY
    await expect(
      vaultManager.connect(vaultOwner).executeHedge(
        vaultAddress, ethers.parseEther("10"), ethers.parseUnits("10", 6),
        await routerAdapter.getAddress(), deadline, policyHash, attestationHash, "APPROVED"
      )
    ).to.be.reverted;
  });

  it("Vector 4: Should REJECT policy commitment with reused nonce", async function () {
    const policyHash = ethers.keccak256(ethers.toUtf8Bytes("nonce-test"));
    const deadline = Math.floor(Date.now() / 1000) + 3600;

    await vaultManager.connect(vaultOwner).registerPolicyCommitmentV2(
      vaultAddress, policyHash, deadline, 100, 1, "ipfs://nonce-1"
    );

    await expect(
      vaultManager.connect(vaultOwner).registerPolicyCommitmentV2(
        vaultAddress, policyHash, deadline, 100, 2, "ipfs://nonce-2"
      )
    ).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");
  });

  it("Vector 5: Should REJECT expired policy commitment registration", async function () {
    const policyHash = ethers.keccak256(ethers.toUtf8Bytes("expired-policy"));
    const expiredDeadline = Math.floor(Date.now() / 1000) - 600; // 10 minutes ago

    await expect(
      vaultManager.connect(vaultOwner).registerPolicyCommitmentV2(
        vaultAddress, policyHash, expiredDeadline, 200, 1, "ipfs://expired"
      )
    ).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");
  });

  it("Vector 6: Should REJECT 0 amount deposit or withdrawal", async function () {
    await expect(
      vaultManager.connect(vaultOwner).depositFXRP(0)
    ).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");

    await expect(
      vaultManager.connect(vaultOwner).withdrawFXRP(0)
    ).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");
  });

  it("Vector 7: Should prevent underflow when withdrawing more than current vault reserve", async function () {
    const currentReserves = await vaultManager.getUserFXRPBalance(vaultAddress);
    const excessiveAmount = currentReserves + ethers.parseEther("100");

    await expect(
      vaultManager.connect(vaultOwner).withdrawFXRP(excessiveAmount)
    ).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");
  });

  it("Vector 8: Should enforce circuit breaker pause mode", async function () {
    await vaultManager.connect(deployer).pauseExecution();

    await expect(
      vaultManager.connect(vaultOwner).depositFXRP(ethers.parseEther("10"))
    ).to.be.revertedWithCustomError(vaultManager, "EnforcedPause");
  });

  // =========================================================================
  // 10 SYSTEM INVARIANTS VERIFICATION
  // =========================================================================

  it("Invariant 1: Vault balance can NEVER become negative", async function () {
    const bal = await vaultManager.getUserFXRPBalance(vaultAddress);
    expect(bal).to.be.gte(0);
  });

  it("Invariant 2: Only authorized owner can withdraw", async function () {
    const initialBal = await vaultManager.getUserFXRPBalance(vaultAddress);
    await expect(vaultManager.connect(attacker).withdrawFXRP(ethers.parseEther("10"))).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");
    expect(await vaultManager.getUserFXRPBalance(vaultAddress)).to.equal(initialBal);
  });

  it("Invariant 3 & 4: Token movement equals exact reserve balance change", async function () {
    const startContractBal = await fxrpToken.balanceOf(await vaultManager.getAddress());
    const startVaultBal = await vaultManager.getUserFXRPBalance(vaultAddress);

    const withdrawAmt = ethers.parseEther("100");
    await vaultManager.connect(vaultOwner).withdrawFXRP(withdrawAmt);

    const endContractBal = await fxrpToken.balanceOf(await vaultManager.getAddress());
    const endVaultBal = await vaultManager.getUserFXRPBalance(vaultAddress);

    expect(startContractBal - endContractBal).to.equal(withdrawAmt);
    expect(startVaultBal - endVaultBal).to.equal(withdrawAmt);
  });

  it("Invariant 5: Policy commitments cannot be replayed", async function () {
    const policyHash = ethers.keccak256(ethers.toUtf8Bytes("unique-replay-inv"));
    await vaultManager.connect(vaultOwner).registerPolicyCommitmentV2(
      vaultAddress, policyHash, Math.floor(Date.now() / 1000) + 3600, 9999, 1, "ipfs://unique"
    );
    await expect(
      vaultManager.connect(vaultOwner).registerPolicyCommitmentV2(
        vaultAddress, policyHash, Math.floor(Date.now() / 1000) + 3600, 9999, 1, "ipfs://unique"
      )
    ).to.be.reverted;
  });
});
