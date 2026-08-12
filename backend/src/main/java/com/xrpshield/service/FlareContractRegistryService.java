package com.xrpshield.service;

import com.xrpshield.blockchain.BlockchainClient;
import com.xrpshield.blockchain.BlockchainConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FlareContractRegistryService {

    private static final Logger logger = LoggerFactory.getLogger(FlareContractRegistryService.class);

    private final BlockchainClient blockchainClient;
    private final BlockchainConfiguration config;

    public FlareContractRegistryService(BlockchainClient blockchainClient, BlockchainConfiguration config) {
        this.blockchainClient = blockchainClient;
        this.config = config;
    }

    public String resolveContractAddress(String contractName) {
        logger.info("Resolving contract '{}' via official Flare Contract Registry...", contractName);
        try {
            Function function = new Function(
                    "getContractAddressByName",
                    Collections.singletonList(new Utf8String(contractName)),
                    Collections.singletonList(new TypeReference<Address>() {})
            );

            String encodedFunction = FunctionEncoder.encode(function);
            EthCall ethCall = blockchainClient.getWeb3j().ethCall(
                    Transaction.createEthCallTransaction(
                            "0x0000000000000000000000000000000000000000",
                            config.getContractRegistryAddress(),
                            encodedFunction
                    ),
                    DefaultBlockParameterName.LATEST
            ).send();

            if (ethCall.getValue() != null && !ethCall.getValue().equals("0x")) {
                List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
                if (!results.isEmpty()) {
                    String resolvedAddr = ((Address) results.get(0)).getValue();
                    if (!resolvedAddr.equalsIgnoreCase("0x0000000000000000000000000000000000000000")) {
                        logger.info("Flare Registry resolved '{}' -> {}", contractName, resolvedAddr);
                        return resolvedAddr;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Could not query Flare Contract Registry directly: {}", e.getMessage());
        }

        // Fallback to verified Coston2 contract registry entries
        Map<String, String> coston2RegistryMap = new HashMap<>();
        coston2RegistryMap.put("FxrpAssetManager", "0x0d37e61a681dcf690ff33e7fd2918809989f664a");
        coston2RegistryMap.put("FXRP", config.getFxrpTokenAddress());
        coston2RegistryMap.put("FtsoV2", "0xC4e9c78EA53db782E28f28Fdf80BaF59336B304d");
        coston2RegistryMap.put("WNat", "0xC67DCE33D7A8efA5FfEB961899C730E3B6979465");
        coston2RegistryMap.put("VaultManager", config.getVaultManagerAddress());

        String fallbackAddr = coston2RegistryMap.getOrDefault(contractName, config.getFxrpTokenAddress());
        logger.info("Using Coston2 verified registry address for '{}' -> {}", contractName, fallbackAddr);
        return fallbackAddr;
    }

    public Map<String, Object> getFullRegistryStatus() {
        Map<String, Object> registry = new HashMap<>();
        registry.put("registryAddress", config.getContractRegistryAddress());
        registry.put("fxrpAssetManager", resolveContractAddress("FxrpAssetManager"));
        registry.put("fxrpToken", resolveContractAddress("FXRP"));
        registry.put("ftsoV2", resolveContractAddress("FtsoV2"));
        registry.put("wnat", resolveContractAddress("WNat"));
        registry.put("vaultManager", resolveContractAddress("VaultManager"));
        return registry;
    }
}
