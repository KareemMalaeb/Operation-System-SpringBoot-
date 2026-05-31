package com.example.OperationSystem.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SendToClientRequest {
    private BigDecimal sellingPrice;
    private String sellingCurrency;
    private String clientOfferNotes;
}