package com.example.OperationSystem.entity;

import java.time.LocalDateTime;

import com.example.OperationSystem.enums.InquiryStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "status_history")

public class StatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Inquiry inquiry;

    @Column(name = "from_status")
    private InquiryStatus fromStatus;

    @Column(name = "to_status")
    private InquiryStatus toStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "changed_by")
    private User changedBy;
    
    private String note;
    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }

}
