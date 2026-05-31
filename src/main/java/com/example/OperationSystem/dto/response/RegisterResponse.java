package com.example.OperationSystem.dto.response;


import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RegisterResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private BigDecimal monthlyTarget;
    private BigDecimal yearlyTarget;
}
