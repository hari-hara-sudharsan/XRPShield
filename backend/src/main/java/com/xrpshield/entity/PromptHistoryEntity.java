package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "prompt_histories")
public class PromptHistoryEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private AIConversationEntity conversation;

    @Column(name = "user_prompt", nullable = false, columnDefinition = "TEXT")
    private String userPrompt;

    @Column(name = "ai_response", nullable = false, columnDefinition = "TEXT")
    private String aiResponse;

    @Column(name = "tokens_used", nullable = false)
    private Integer tokensUsed = 0;

    public PromptHistoryEntity() {}

    public PromptHistoryEntity(AIConversationEntity conversation, String userPrompt, String aiResponse, Integer tokensUsed) {
        this.conversation = conversation;
        this.userPrompt = userPrompt;
        this.aiResponse = aiResponse;
        this.tokensUsed = tokensUsed != null ? tokensUsed : 0;
    }

    public AIConversationEntity getConversation() {
        return conversation;
    }

    public void setConversation(AIConversationEntity conversation) {
        this.conversation = conversation;
    }

    public String getUserPrompt() {
        return userPrompt;
    }

    public void setUserPrompt(String userPrompt) {
        this.userPrompt = userPrompt;
    }

    public String getAiResponse() {
        return aiResponse;
    }

    public void setAiResponse(String aiResponse) {
        this.aiResponse = aiResponse;
    }

    public Integer getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(Integer tokensUsed) {
        this.tokensUsed = tokensUsed;
    }
}
