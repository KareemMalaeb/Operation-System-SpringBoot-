package com.example.OperationSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OperationSystem.entity.Notification;
import com.example.OperationSystem.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);

    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient);

    long countByRecipientAndIsReadFalse(User recipient);
}