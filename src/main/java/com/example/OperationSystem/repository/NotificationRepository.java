package com.example.OperationSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OperationSystem.entity.Notification;
import com.example.OperationSystem.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // All notifications, newest first 
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    // Unread notifications
    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient);
    // Count of unread notifications
    long countByRecipientAndIsReadFalse(User recipient);
}