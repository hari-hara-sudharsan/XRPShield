package com.xrpshield.dto;

import java.time.Instant;
import java.util.Map;

public class SystemInfoResponseDto {

    private String name;
    private String version;
    private String environment;
    private String javaVersion;
    private String springProfile;
    private Instant serverTime;
    private Map<String, String> buildDetails;

    public SystemInfoResponseDto() {
        this.serverTime = Instant.now();
    }

    public SystemInfoResponseDto(String name, String version, String environment, String javaVersion, String springProfile, Instant serverTime, Map<String, String> buildDetails) {
        this.name = name;
        this.version = version;
        this.environment = environment;
        this.javaVersion = javaVersion;
        this.springProfile = springProfile;
        this.serverTime = serverTime != null ? serverTime : Instant.now();
        this.buildDetails = buildDetails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getSpringProfile() {
        return springProfile;
    }

    public void setSpringProfile(String springProfile) {
        this.springProfile = springProfile;
    }

    public Instant getServerTime() {
        return serverTime;
    }

    public void setServerTime(Instant serverTime) {
        this.serverTime = serverTime;
    }

    public Map<String, String> getBuildDetails() {
        return buildDetails;
    }

    public void setBuildDetails(Map<String, String> buildDetails) {
        this.buildDetails = buildDetails;
    }
}
