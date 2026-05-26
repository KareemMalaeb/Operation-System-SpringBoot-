package com.example.OperationSystem.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SelectQuoteRequest {
    private Long quotationId;
    private BigDecimal sellingPrice;
}