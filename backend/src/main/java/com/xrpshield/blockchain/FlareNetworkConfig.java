package com.xrpshield.blockchain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlareNetworkConfig {

    @Value("${xrpshield.flare.rpc-url:https://coston2-api.flare.network/ext/C/rpc}")
    private String rpcUrl;

    @Value("${xrpshield.flare.chain-id:114}")
    private String chainId;

    public String getRpcUrl() {
        return rpcUrl;
    }

    public void setRpcUrl(String rpcUrl) {
        this.rpcUrl = rpcUrl;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }
}
