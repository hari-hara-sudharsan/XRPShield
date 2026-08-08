package com.xrpshield.service;

import com.xrpshield.exception.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class AIValidationService {

    private static final int MAX_PROMPT_LENGTH = 4000;

    public void validatePromptLength(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new ValidationException("Prompt input cannot be empty");
        }
        if (prompt.length() > MAX_PROMPT_LENGTH) {
            throw new ValidationException("Prompt exceeds maximum allowed length of " + MAX_PROMPT_LENGTH + " characters");
        }
    }
}
