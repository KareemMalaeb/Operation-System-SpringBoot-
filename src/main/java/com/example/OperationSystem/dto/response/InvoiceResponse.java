package com.example.OperationSystem.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.OperationSystem.entity.Invoice;

import lombok.Data;

@Data
public class InvoiceResponse {

    private long id;
    private String invoiceNumber;
    private String inquiryCode;

    private String clientName;
    private String clientEmail;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String currency;
    private BigDecimal amount;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String notes;
    private String status;
    private LocalDate createdAt;

    public static InvoiceResponse from(Invoice i) {
        InvoiceResponse r = new InvoiceResponse();
        r.setId(i.getId());
        r.setInvoiceNumber(i.getInvoiceNumber());
        r.setInquiryCode(i.getInquiry().getCode());
        r.setClientName(i.getClientName());
        r.setClientEmail(i.getClientEmail());
        r.setIssueDate(i.getIssueDate());
        r.setDueDate(i.getDueDate());
        r.setCurrency(i.getCurrency());
        r.setAmount(i.getAmount());
        r.setTaxRate(i.getTaxRate());
        r.setTaxAmount(i.getTaxAmount());
        r.setTotalAmount(i.getTotalAmount());
        r.setNotes(i.getNotes());
        r.setStatus(i.getStatus().name());
        r.setCreatedAt(i.getCreatedAt());
        return r;
    }

}
