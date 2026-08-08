package com.xrpshield.service;

import com.xrpshield.dto.SessionResponseDto;
import com.xrpshield.entity.SessionEntity;
import com.xrpshield.entity.SessionStatus;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.exception.ResourceNotFoundException;
import com.xrpshield.mapper.SessionMapper;
import com.xrpshield.repository.SessionRepository;
import com.xrpshield.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class SessionService {

    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SessionMapper sessionMapper;

    public SessionService(SessionRepository sessionRepository, UserRepository userRepository, SessionMapper sessionMapper) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.sessionMapper = sessionMapper;
    }

    public SessionResponseDto createSession(UUID userId, String sessionToken, String nonce, Instant expiresAt) {
        logger.info("Creating session token for user {}", userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        SessionEntity session = new SessionEntity(user, sessionToken, nonce, SessionStatus.ACTIVE, expiresAt);
        SessionEntity saved = sessionRepository.save(session);
        return sessionMapper.toDto(saved);
    }

    public SessionResponseDto getSessionByToken(String sessionToken) {
        SessionEntity session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new ResourceNotFoundException("Session", "sessionToken", sessionToken));
        return sessionMapper.toDto(session);
    }

    public SessionResponseDto revokeSession(UUID id) {
        SessionEntity session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session", "id", id));
        session.setStatus(SessionStatus.REVOKED);
        SessionEntity updated = sessionRepository.save(session);
        return sessionMapper.toDto(updated);
    }
}
