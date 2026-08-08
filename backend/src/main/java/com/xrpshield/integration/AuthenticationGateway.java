package com.xrpshield.integration;

import com.xrpshield.security.JwtTokenProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationGateway {

    private final JwtTokenProvider tokenProvider;

    public AuthenticationGateway(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public JwtTokenProvider getTokenProvider() {
        return tokenProvider;
    }

    public Map<String, Object> getAuthenticationSummary() {

        Map<String, Object> summary = new HashMap<>();
        summary.put("passwordScheme", "BCrypt ($2a$10)");
        summary.put("tokenScheme", "HMAC-SHA256 JWT");
        summary.put("walletAuth", "EIP-191 Web3 Signatures");
        summary.put("rolesSupported", new String[]{"ROLE_USER", "ROLE_ADMIN"});
        return summary;
    }
}
