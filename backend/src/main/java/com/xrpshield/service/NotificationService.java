package com.xrpshield.service;

import com.xrpshield.dto.NotificationRequestDto;
import com.xrpshield.dto.NotificationResponseDto;
import com.xrpshield.entity.NotificationEntity;
import com.xrpshield.entity.NotificationSeverity;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.exception.ResourceNotFoundException;
import com.xrpshield.mapper.NotificationMapper;
import com.xrpshield.repository.NotificationRepository;
import com.xrpshield.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
    }

    public NotificationResponseDto sendNotification(NotificationRequestDto request) {
        logger.info("Sending notification '{}' to user {}", request.getTitle(), request.getUserId());
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        NotificationEntity notification = new NotificationEntity(
                user,
                request.getTitle(),
                request.getMessage(),
                request.getSeverity() != null ? request.getSeverity() : NotificationSeverity.INFO,
                false
        );
        NotificationEntity saved = notificationRepository.save(notification);
        return notificationMapper.toDto(saved);
    }

    public List<NotificationResponseDto> getNotificationsByUserId(UUID userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public NotificationResponseDto markAsRead(UUID notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        notification.setRead(true);
        NotificationEntity updated = notificationRepository.save(notification);
        return notificationMapper.toDto(updated);
    }
}
