package com.xrpshield.service;

import com.xrpshield.entity.AIConversationEntity;
import com.xrpshield.entity.PromptHistoryEntity;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.repository.AIConversationRepository;
import com.xrpshield.repository.PromptHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {

    private final AIConversationRepository conversationRepository;
    private final PromptHistoryRepository promptHistoryRepository;

    public ConversationService(AIConversationRepository conversationRepository, PromptHistoryRepository promptHistoryRepository) {
        this.conversationRepository = conversationRepository;
        this.promptHistoryRepository = promptHistoryRepository;
    }

    @Transactional
    public AIConversationEntity getOrCreateConversation(UserEntity user, String title) {
        List<AIConversationEntity> existing = conversationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        AIConversationEntity conversation = new AIConversationEntity(user, title != null ? title : "AI Treasury Session");
        return conversationRepository.save(conversation);
    }

    @Transactional
    public void recordPromptHistory(AIConversationEntity conversation, String userPrompt, String aiResponse, int tokensUsed) {
        PromptHistoryEntity history = new PromptHistoryEntity(conversation, userPrompt, aiResponse, tokensUsed);
        promptHistoryRepository.save(history);
    }

    public List<AIConversationEntity> getUserConversations(UserEntity user) {
        return conversationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public List<PromptHistoryEntity> getConversationHistory(UUID conversationId) {
        return promptHistoryRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }
}
