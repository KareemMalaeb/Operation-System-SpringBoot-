package com.example.OperationSystem.dto.response;

import java.time.LocalDateTime;

import com.example.OperationSystem.entity.Notification;

import lombok.Data;

@Data
public class NotificationResponse {

    private Long id;
    private String message;
    private Long inquiryId;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setMessage(notification.getMessage());
        response.setInquiryId(notification.getInquiryId());
        response.setRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}