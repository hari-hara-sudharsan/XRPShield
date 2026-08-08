package com.xrpshield.repository;

import com.xrpshield.entity.PromptHistoryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PromptHistoryRepository extends BaseRepository<PromptHistoryEntity> {

    List<PromptHistoryEntity> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);
}
