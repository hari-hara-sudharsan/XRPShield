package com.xrpshield;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class XrpShieldApplicationTests {

    @Test
    @DisplayName("Should verify main application class loads")
    void contextLoads() {
        XrpShieldApplication application = new XrpShieldApplication();
        assertNotNull(application);
    }
}
