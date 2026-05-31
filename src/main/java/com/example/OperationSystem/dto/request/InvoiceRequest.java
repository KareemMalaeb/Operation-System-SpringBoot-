package com.example.OperationSystem.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class InvoiceRequest {
    private long inquiryId;
    private String clientName;
    private String clientEmail;
    
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String currency;
    
    private BigDecimal amount;
    private BigDecimal taxRate;
    private String notes;
}
