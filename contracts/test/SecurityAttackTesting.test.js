const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("XRPShield Phase 5 Sprint 2: Security Attack Testing & Adversarial Vectors Suite", function () {
  let deployer, extensionSigner, attacker, user, vault2User;
  let accessManager, treasuryStorage, vaultManager, extensionAdapter;
  let fxrpToken, usdt0Token, routerAdapter;
  let vaultAddress, vault2Address;

  let validPolicyHash, validAttestationHash, validSignature, validResultStruct;
  let deadline, nonce;

  beforeEach(async function () {
    [deployer, extensionSigner, attacker, user, vault2User] = await ethers.getSigners();
    vaultAddress = user.address;
    vault2Address = vault2User.address;

    // 1. Deploy Core Contracts
    const TestFXRPToken = await ethers.getContractFactory("TestFXRPToken");
    fxrpToken = await TestFXRPToken.deploy();
    await fxrpToken.waitForDeployment();

    const TestUSDT0Token = await ethers.getContractFactory("TestUSDT0Token");
    usdt0Token = await TestUSDT0Token.deploy();
    await usdt0Token.waitForDeployment();

    const DEXRouterAdapter = await ethers.getContractFactory("DEXRouterAdapter");
    routerAdapter = await DEXRouterAdapter.deploy(await fxrpToken.getAddress(), await usdt0Token.getAddress());
    await routerAdapter.waitForDeployment();
    await usdt0Token.transfer(await routerAdapter.getAddress(), ethers.parseUnits("1000000", 6));

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

    // Register Vault & Policy
    await vaultManager.connect(user).registerVault(vaultAddress, "User Vault", "FXRP");
    await vaultManager.connect(vault2User).registerVault(vault2Address, "Vault 2", "FXRP");

    deadline = Math.floor(Date.now() / 1000) + 3600;
    nonce = 7001;

    const canonicalPayloadStr = JSON.stringify({
      vaultAddress: vaultAddress.toLowerCase(),
      asset: "FXRP",
      hedgeRatio: "0.7000",
      triggerThreshold: "5.00",
      maximumProtection: "100000.0",
      deadline: deadline,
      nonce: nonce,
      policyVersion: 1
    });

    validPolicyHash = ethers.keccak256(ethers.toUtf8Bytes(canonicalPayloadStr));
    await vaultManager.connect(user).registerPolicyCommitmentV2(
      vaultAddress, validPolicyHash, deadline, nonce, 1, "ipfs://valid-policy"
    );

    validAttestationHash = ethers.keccak256(ethers.toUtf8Bytes("valid-attestation-hash"));

    const network = await ethers.provider.getNetwork();
    const domain = {
      name: "XRPShield FCC Extension",
      version: "1",
      chainId: network.chainId,
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

    const messageStruct = {
      vaultAddress: vaultAddress,
      policyHash: validPolicyHash,
      status: "APPROVED",
      attestationHash: validAttestationHash,
      nonce: nonce,
      timestamp: Math.floor(Date.now() / 1000),
      deadline: deadline
    };

    validSignature = await extensionSigner.signTypedData(domain, types, messageStruct);

    validResultStruct = {
      success: true,
      status: "APPROVED",
      rationale: "Approved inside TEE",
      policyHash: validPolicyHash,
      attestationHash: validAttestationHash,
      nonce: nonce,
      timestamp: messageStruct.timestamp,
      deadline: deadline,
      signature: validSignature
    };
  });

  it("1. Attack Vector 1: Replay Attack (Resubmitting executed attestation)", async function () {
    const fxrpAmountIn = ethers.parseEther("100");
    const minUsdtOut = ethers.parseUnits("100", 6);

    // Initial execution succeeds
    await vaultManager.connect(user).executeHedge(
      vaultAddress, fxrpAmountIn, minUsdtOut, await routerAdapter.getAddress(),
      deadline, validPolicyHash, validAttestationHash, "APPROVED"
    );

    // Replay attempt with identical attestation hash reverts
    await expect(
      vaultManager.connect(user).executeHedge(
        vaultAddress, fxrpAmountIn, minUsdtOut, await routerAdapter.getAddress(),
        deadline, validPolicyHash, validAttestationHash, "APPROVED"
      )
    ).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");
  });

  it("2. Attack Vector 2: Mismatched / Fake Policy Commitment", async function () {
    const fakePolicyHash = ethers.keccak256(ethers.toUtf8Bytes("fake-unregistered-policy"));

    await expect(
      vaultManager.connect(user).executeHedge(
        vaultAddress, ethers.parseEther("100"), ethers.parseUnits("100", 6),
        await routerAdapter.getAddress(), deadline, fakePolicyHash, validAttestationHash, "APPROVED"
      )
    ).to.be.revertedWithCustomError(vaultManager, "UnauthorizedCaller");
  });

  it("3. Attack Vector 3: Wrong Vault Target (Attestation replay across vaults)", async function () {
    // Attempting to execute vault 1 attestation against vault 2 reverts for unauthorized caller or hash mismatch
    await expect(
      vaultManager.connect(user).executeHedge(
        vault2Address, ethers.parseEther("100"), ethers.parseUnits("100", 6),
        await routerAdapter.getAddress(), deadline, validPolicyHash, validAttestationHash, "APPROVED"
      )
    ).to.be.revertedWithCustomError(vaultManager, "UnauthorizedCaller");
  });

  it("4. Attack Vector 4: Unauthorized Wallet Signer (Attacker invoking executeHedge)", async function () {
    await expect(
      vaultManager.connect(attacker).executeHedge(
        vaultAddress, ethers.parseEther("100"), ethers.parseUnits("100", 6),
        await routerAdapter.getAddress(), deadline, validPolicyHash, validAttestationHash, "APPROVED"
      )
    ).to.be.revertedWithCustomError(vaultManager, "UnauthorizedCaller");
  });

  it("5. Attack Vector 5: Expired Attestation Deadline", async function () {
    const expiredDeadline = Math.floor(Date.now() / 1000) - 100;
    const expiredResult = { ...validResultStruct, deadline: expiredDeadline };

    const verified = await extensionAdapter.verifyAttestationView(vaultAddress, expiredResult);
    expect(verified).to.be.false;
  });

  it("6. Attack Vector 6: Corrupted EIP-712 Signature Bytes", async function () {
    const corruptedSignature = validSignature.substring(0, validSignature.length - 4) + "0000";
    const corruptedResult = { ...validResultStruct, signature: corruptedSignature };

    const verified = await extensionAdapter.verifyAttestationView(vaultAddress, corruptedResult);
    expect(verified).to.be.false;
  });

  it("7. Attack Vector 7: Wrong Chain ID (chainId = 1 instead of 114)", async function () {
    const wrongDomain = {
      name: "XRPShield FCC Extension",
      version: "1",
      chainId: 1, // Wrong chain ID
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

    const messageStruct = {
      vaultAddress: vaultAddress,
      policyHash: validPolicyHash,
      status: "APPROVED",
      attestationHash: validAttestationHash,
      nonce: nonce,
      timestamp: Math.floor(Date.now() / 1000),
      deadline: deadline
    };

    const wrongChainSig = await extensionSigner.signTypedData(wrongDomain, types, messageStruct);
    const wrongChainResult = { ...validResultStruct, signature: wrongChainSig };

    const verified = await extensionAdapter.verifyAttestationView(vaultAddress, wrongChainResult);
    expect(verified).to.be.false;
  });

  it("8. Attack Vector 8: Wrong Verifying Contract Address", async function () {
    const wrongDomain = {
      name: "XRPShield FCC Extension",
      version: "1",
      chainId: 114,
      verifyingContract: attacker.address // Wrong contract
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

    const messageStruct = {
      vaultAddress: vaultAddress,
      policyHash: validPolicyHash,
      status: "APPROVED",
      attestationHash: validAttestationHash,
      nonce: nonce,
      timestamp: Math.floor(Date.now() / 1000),
      deadline: deadline
    };

    const wrongContractSig = await extensionSigner.signTypedData(wrongDomain, types, messageStruct);
    const wrongContractResult = { ...validResultStruct, signature: wrongContractSig };

    const verified = await extensionAdapter.verifyAttestationView(vaultAddress, wrongContractResult);
    expect(verified).to.be.false;
  });

  it("9. Attack Vector 9: Duplicate Execution Prevention", async function () {
    const attestation2Hash = ethers.keccak256(ethers.toUtf8Bytes("attestation-2"));
    const fxrpAmountIn = ethers.parseEther("100");

    await vaultManager.connect(user).executeHedge(
      vaultAddress, fxrpAmountIn, ethers.parseUnits("100", 6),
      await routerAdapter.getAddress(), deadline, validPolicyHash, attestation2Hash, "APPROVED"
    );

    // Repeated call with same attestation hash reverts
    await expect(
      vaultManager.connect(user).executeHedge(
        vaultAddress, fxrpAmountIn, ethers.parseUnits("100", 6),
        await routerAdapter.getAddress(), deadline, validPolicyHash, attestation2Hash, "APPROVED"
      )
    ).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");
  });

  it("10. Attack Vector 10: Expired Swap Deadline", async function () {
    const expiredSwapDeadline = Math.floor(Date.now() / 1000) - 50;

    await expect(
      vaultManager.connect(user).executeHedge(
        vaultAddress, ethers.parseEther("100"), ethers.parseUnits("100", 6),
        await routerAdapter.getAddress(), expiredSwapDeadline, validPolicyHash,
        ethers.keccak256(ethers.toUtf8Bytes("att-expired")), "APPROVED"
      )
    ).to.be.revertedWithCustomError(vaultManager, "InvalidParameters");
  });
});
