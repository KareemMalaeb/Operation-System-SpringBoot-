package com.example.OperationSystem.dto.response;

import java.math.BigDecimal;

import com.example.OperationSystem.entity.Quotation;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuotationResponse {

    private Long id;
    private String agentName;
    private String agentEmail;
    private BigDecimal price;
    private String currency;
    private String transitTime;
    private String remarks;
    private Boolean isSelected;
    private BigDecimal sellingPrice;
    private String createdAt;

    public static QuotationResponse from(Quotation q) {
        return QuotationResponse.builder()
                .id(q.getId())
                .agentName(q.getAgent() != null ? q.getAgent().getName() : null)
                .agentEmail(q.getAgent() != null ? q.getAgent().getEmail() : null)
                .price(q.getPrice())
                .currency(q.getCurrency())
                .transitTime(q.getTransitTime())
                .remarks(q.getRemarks())
                .isSelected(q.getIsSelected())
                .sellingPrice(q.getSellingPrice())
                .createdAt(q.getCreatedAt() != null ? q.getCreatedAt().toString() : null)
                .build();
    }
}