package com.example.OperationSystem.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class AddQuotationRequest {
    @NotNull(message = "Agent ID is required")
    private Long agentId;
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    private String Currency;
    private String TransitTime;
    private String Validity;
    private String Remarks;


    
}
