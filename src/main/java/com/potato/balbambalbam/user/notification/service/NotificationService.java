package com.potato.balbambalbam.user.notification.service;

import com.potato.balbambalbam.data.entity.Notification;
import com.potato.balbambalbam.data.entity.NotificationRead;
import com.potato.balbambalbam.data.repository.NotificationReadRepository;
import com.potato.balbambalbam.data.repository.NotificationRepository;
import com.potato.balbambalbam.user.notification.dto.NotificationDto;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationReadRepository notificationReadRepository;

    public List<NotificationDto> getActiveNotifications(Long userId) {
        // 활성화된 알림을 ID 내림차순으로 가져옴 (id가 큰 순)
        List<Notification> activeNotifications = notificationRepository.findByIsActiveTrueOrderByIdDesc();

        // 사용자가 읽은 알림 ID 목록을 가져옴
        Set<Long> readNotificationIds = notificationReadRepository.findByUserId(userId)
                .stream()
                .map(NotificationRead::getNotificationId)
                .collect(Collectors.toSet());

        // DTO로 변환하면서 읽음 여부 설정
        return activeNotifications.stream()
                .map(notification -> {
                    NotificationDto dto = new NotificationDto();
                    dto.setId(notification.getId());
                    dto.setTitle(notification.getTitle());
                    dto.setContent(notification.getContent());
                    dto.setUnread(!readNotificationIds.contains(notification.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        // 이미 읽은 알림이 아닌 경우에만 저장
        if (!notificationReadRepository.existsByNotificationIdAndUserId(notificationId, userId)) {
            NotificationRead notificationRead = new NotificationRead();
            notificationRead.setNotificationId(notificationId);
            notificationRead.setUserId(userId);
            notificationReadRepository.save(notificationRead);
        }
    }

    public boolean hasUnreadNotifications(Long userId) {
        // 활성화된 전체 알림 수 조회
        List<Notification> activeNotifications = notificationRepository.findByIsActiveTrueOrderByIdDesc();
        Set<Long> allActiveNotificationIds = activeNotifications.stream()
                .map(Notification::getId)
                .collect(Collectors.toSet());

        // 사용자가 읽은 알림 ID 목록
        Set<Long> readNotificationIds = notificationReadRepository.findByUserId(userId)
                .stream()
                .map(NotificationRead::getNotificationId)
                .collect(Collectors.toSet());

        // 읽지 않은 알림이 하나라도 있으면 true
        return allActiveNotificationIds.stream()
                .anyMatch(id -> !readNotificationIds.contains(id));
    }
}