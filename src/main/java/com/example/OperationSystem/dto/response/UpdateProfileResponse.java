package com.example.OperationSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private java.math.BigDecimal monthlyTarget;
    private java.math.BigDecimal yearlyTarget;
}