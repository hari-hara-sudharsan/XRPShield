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
import org.web3j.abi.datatypes.generated.Uint256;

import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FXRPService {

    private static final Logger logger = LoggerFactory.getLogger(FXRPService.class);

    private final BlockchainClient blockchainClient;
    private final BlockchainConfiguration config;

    public FXRPService(BlockchainClient blockchainClient, BlockchainConfiguration config) {
        this.blockchainClient = blockchainClient;
        this.config = config;
    }

    public BigInteger getFXRPBalance(String address) {
        logger.info("Reading REAL Coston2 FXRP ERC-20 balance for address: {}", address);
        try {
            Function function = new Function(
                    "balanceOf",
                    Collections.singletonList(new Address(address)),
                    Collections.singletonList(new TypeReference<Uint256>() {})
            );

            String encodedFunction = FunctionEncoder.encode(function);
            EthCall ethCall = blockchainClient.getWeb3j().ethCall(
                    Transaction.createEthCallTransaction(address, config.getFxrpTokenAddress(), encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send();

            if (ethCall.getValue() != null && !ethCall.getValue().equals("0x")) {
                List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
                if (!results.isEmpty()) {
                    BigInteger balance = ((Uint256) results.get(0)).getValue();
                    logger.info("Real Coston2 FXRP balance for {}: {} raw wei", address, balance);
                    return balance;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to query Coston2 FXRP balance from node: {}", e.getMessage());
        }
        return BigInteger.ZERO;
    }

    public BigInteger getAllowance(String ownerAddress, String spenderAddress) {
        try {
            Function function = new Function(
                    "allowance",
                    Arrays.asList(new Address(ownerAddress), new Address(spenderAddress)),
                    Collections.singletonList(new TypeReference<Uint256>() {})
            );

            String encodedFunction = FunctionEncoder.encode(function);
            EthCall ethCall = blockchainClient.getWeb3j().ethCall(
                    Transaction.createEthCallTransaction(ownerAddress, config.getFxrpTokenAddress(), encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send();

            if (ethCall.getValue() != null && !ethCall.getValue().equals("0x")) {
                List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
                if (!results.isEmpty()) {
                    return ((Uint256) results.get(0)).getValue();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to query Coston2 FXRP allowance from node: {}", e.getMessage());
        }
        return BigInteger.ZERO;
    }

    public Map<String, Object> getFXRPTokenDetails(String address) {
        BigInteger rawBalance = getFXRPBalance(address);
        BigDecimal formattedBalance = new BigDecimal(rawBalance)
                .divide(new BigDecimal("1000000000000000000"), 4, RoundingMode.HALF_UP);

        BigInteger allowanceToVault = getAllowance(address, config.getVaultManagerAddress());
        BigDecimal formattedAllowance = new BigDecimal(allowanceToVault)
                .divide(new BigDecimal("1000000000000000000"), 4, RoundingMode.HALF_UP);

        Map<String, Object> response = new HashMap<>();
        response.put("tokenAddress", config.getFxrpTokenAddress());
        response.put("symbol", "FXRP");
        response.put("name", "Flare Wrapped XRP");
        response.put("decimals", 18);
        response.put("walletAddress", address);
        response.put("rawBalanceWei", rawBalance.toString());
        response.put("formattedBalanceFXRP", formattedBalance.toPlainString());
        response.put("vaultAllowanceWei", allowanceToVault.toString());
        response.put("vaultAllowanceFXRP", formattedAllowance.toPlainString());
        response.put("source", "Flare Coston2 Node eth_call");

        return response;
    }
}
