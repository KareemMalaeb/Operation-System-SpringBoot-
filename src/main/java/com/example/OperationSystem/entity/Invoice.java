package com.example.OperationSystem.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.OperationSystem.enums.InvoiceStatus;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;
    @Column(unique = true)
    private String invoiceNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry; // FK to the WON inquiry
    
    @Column
    private String clientName;
    @Column
    private String clientEmail;
    @Column
    private LocalDate issueDate;
    @Column
    private LocalDate dueDate;
    @Column
    private String currency;
    @Column
    private BigDecimal amount; // freight charge (selling price)
    @Column
    private BigDecimal taxRate; // Optional e.x:15.0
    @Column
    private BigDecimal taxAmount; // calculated= amount x taxRate /100
    @Column
    private BigDecimal totalAmount; // amount + taxAmount
    @Column
    private String notes;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status; //DRAFT, SENT, PAID
    @Column
    private LocalDate createdAt; 


}
