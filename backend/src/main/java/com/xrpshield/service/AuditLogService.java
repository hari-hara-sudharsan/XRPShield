package com.xrpshield.service;

import com.xrpshield.dto.AuditLogResponseDto;
import com.xrpshield.entity.AuditLogEntity;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.mapper.AuditLogMapper;
import com.xrpshield.repository.AuditLogRepository;
import com.xrpshield.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final AuditLogMapper auditLogMapper;

    public AuditLogService(AuditLogRepository auditLogRepository, UserRepository userRepository, AuditLogMapper auditLogMapper) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.auditLogMapper = auditLogMapper;
    }

    public void logAction(UUID userId, String action, String resource, String details, String ipAddress) {
        logger.info("AUDIT_EVENT | User: {} | Action: {} | Resource: {} | Details: {}",
                userId != null ? userId : "SYSTEM", action, resource, details != null ? details : "N/A");

        UserEntity user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        AuditLogEntity log = new AuditLogEntity(user, action, resource, details, ipAddress);
        auditLogRepository.save(log);
    }

    public List<AuditLogResponseDto> getAuditLogsByUserId(UUID userId) {
        return auditLogRepository.findByUserId(userId).stream()
                .map(auditLogMapper::toDto)
                .collect(Collectors.toList());
    }
}
