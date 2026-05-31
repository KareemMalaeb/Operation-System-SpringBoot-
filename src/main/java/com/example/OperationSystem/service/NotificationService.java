package com.example.OperationSystem.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.OperationSystem.dto.response.NotificationResponse;
import com.example.OperationSystem.entity.Notification;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.exceptions.BusinessException;
import com.example.OperationSystem.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createNotification(User recipient, String message, Long inquiryId) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(message)
                .inquiryId(inquiryId)
                .build();
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getAll(User currentUser) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    public List<NotificationResponse> getUnread(User currentUser) {
        return notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(User currentUser) {
        return notificationRepository.countByRecipientAndIsReadFalse(currentUser);
    }

    public void markAsRead(Long id, User currentUser) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Notification not found"));
        if (!notification.getRecipient().getId().equals(currentUser.getId())) {
            throw new BusinessException("Access denied");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(User currentUser) {
        List<Notification> unread = notificationRepository
                .findByRecipientAndIsReadFalseOrderByCreatedAtDesc(currentUser);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}