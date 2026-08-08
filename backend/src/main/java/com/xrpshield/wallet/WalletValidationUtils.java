package com.xrpshield.wallet;

import java.util.regex.Pattern;

public class WalletValidationUtils {

    private static final Pattern ETH_ADDRESS_PATTERN = Pattern.compile("^0x[a-fA-F0-9]{40}$");
    private static final Pattern XRP_ADDRESS_PATTERN = Pattern.compile("^r[0-9a-zA-Z]{24,34}$");

    public static boolean isValidEthereumAddress(String address) {
        if (address == null || address.isBlank()) {
            return false;
        }
        return ETH_ADDRESS_PATTERN.matcher(address).matches();
    }

    public static boolean isValidXrpAddress(String address) {
        if (address == null || address.isBlank()) {
            return false;
        }
        return XRP_ADDRESS_PATTERN.matcher(address).matches();
    }
}
