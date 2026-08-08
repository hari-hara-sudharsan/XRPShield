package com.xrpshield.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    private final ApplicationFacade applicationFacade;

    public ApplicationService(ApplicationFacade applicationFacade) {
        this.applicationFacade = applicationFacade;
    }

    public Map<String, Object> getApplicationInformation() {
        logger.info("Retrieving unified application information");

        Map<String, Object> info = new HashMap<>();
        info.put("applicationName", "XRPShield");
        info.put("description", "Privacy-Preserving XRP Treasury & Risk Engine on Flare Network");
        info.put("version", "1.0.0");
        info.put("architecture", "Clean Architecture / Layered Facade & Gateway");
        info.put("systemState", applicationFacade.getCompleteSystemState());
        info.put("serverTimestamp", Instant.now());

        return info;
    }
}
