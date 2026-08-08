package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ai_conversations")
public class AIConversationEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    public AIConversationEntity() {}

    public AIConversationEntity(UserEntity user, String title) {
        this.user = user;
        this.title = title;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
