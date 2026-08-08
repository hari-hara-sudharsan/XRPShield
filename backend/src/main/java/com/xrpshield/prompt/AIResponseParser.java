package com.xrpshield.prompt;

import org.springframework.stereotype.Component;

@Component
public class AIResponseParser {

    public String parseStructuredResponse(String rawResponse) {
        if (rawResponse == null) return "No response generated";
        return rawResponse.trim();
    }
}
