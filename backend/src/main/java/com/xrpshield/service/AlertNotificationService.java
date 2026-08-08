package com.xrpshield.service;

import com.xrpshield.entity.PlatformNotificationEntity;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.repository.PlatformNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(AlertNotificationService.class);

    private final PlatformNotificationRepository notificationRepository;

    public AlertNotificationService(PlatformNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void dispatchAlert(UserEntity user, String severity, String title, String message) {
        logger.info("SYSTEM_ALERT | Severity: {} | Title: {} | Message: {}", severity, title, message);
        PlatformNotificationEntity notif = new PlatformNotificationEntity(user, severity, title, message, false);
        notificationRepository.save(notif);
    }

    public List<PlatformNotificationEntity> getUserNotifications(UserEntity user) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }
}
