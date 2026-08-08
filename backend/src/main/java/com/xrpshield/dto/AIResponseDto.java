package com.xrpshield.dto;

import java.time.Instant;
import java.util.UUID;

public class AIResponseDto {

    private UUID id;
    private String responseType;
    private String content;
    private Integer tokensUsed;
    private Instant createdAt;

    public AIResponseDto() {}

    public AIResponseDto(UUID id, String responseType, String content, Integer tokensUsed, Instant createdAt) {
        this.id = id;
        this.responseType = responseType;
        this.content = content;
        this.tokensUsed = tokensUsed;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(Integer tokensUsed) {
        this.tokensUsed = tokensUsed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
