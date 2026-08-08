package com.xrpshield.validator;

import com.xrpshield.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VaultValidator {

    public void validatePositiveAmount(BigDecimal amount, String fieldName) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(fieldName + " must be greater than zero");
        }
    }

    public void validateAddress(String address, String fieldName) {
        if (address == null || address.trim().isEmpty()) {
            throw new ValidationException(fieldName + " address cannot be empty");
        }
    }
}
