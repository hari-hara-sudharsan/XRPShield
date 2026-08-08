package com.xrpshield;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableJpaRepositories("com.xrpshield.repository")
@EntityScan("com.xrpshield.entity")
public class XrpShieldApplication {

    public static void main(String[] args) {
        SpringApplication.run(XrpShieldApplication.class, args);
    }
}

