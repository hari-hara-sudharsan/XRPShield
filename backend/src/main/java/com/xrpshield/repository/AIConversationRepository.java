package com.xrpshield.repository;

import com.xrpshield.entity.AIConversationEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AIConversationRepository extends BaseRepository<AIConversationEntity> {

    List<AIConversationEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
