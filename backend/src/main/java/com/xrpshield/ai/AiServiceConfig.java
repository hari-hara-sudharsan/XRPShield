package com.xrpshield.ai;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AiServiceConfig {

    @Value("${xrpshield.openai.api-key:disabled}")
    private String apiKey;

    public boolean isAiEnabled() {
        return apiKey != null && !apiKey.isBlank() && !"disabled".equalsIgnoreCase(apiKey);
    }
}
