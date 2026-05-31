package com.example.OperationSystem.dto.request;

import java.math.BigDecimal;

import com.example.OperationSystem.enums.Role;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
    private BigDecimal monthlyTarget;
    private BigDecimal yearlyTarget;
}
