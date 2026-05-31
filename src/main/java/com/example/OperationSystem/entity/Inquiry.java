package com.example.OperationSystem.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.OperationSystem.enums.FreightType;
import com.example.OperationSystem.enums.InquiryStatus;
import com.example.OperationSystem.enums.ContainerType;
import com.example.OperationSystem.enums.Encoterms;
import com.example.OperationSystem.enums.DG;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inquiries")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;
    @Column(nullable = false)
    private String clientName;
    @Column(nullable = false)
    private String clientEmail;
    @Column(nullable = false)
    private String clientPhone;
    @Column(nullable = false)
    private String origin;
    @Column(nullable = false)
    private String destination;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private InquiryStatus status = InquiryStatus.NEW;
    
    @Enumerated(EnumType.STRING)
    private Encoterms encoterms;
    
    private String supplierLocation;

    @Enumerated(EnumType.STRING)
    private DG dg;
    
    @Enumerated(EnumType.STRING)
    private FreightType freightType;

    @Enumerated(EnumType.STRING)
    private ContainerType containerType;

    private String plFilePath;
    private String ciFilePath;

    private String additionalDetails;
    
    @Column
    private BigDecimal sellingPrice;

    @Column(length = 10)
    private String sellingCurrency;

    @Column(columnDefinition = "TEXT")
    private String clientOfferNotes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
