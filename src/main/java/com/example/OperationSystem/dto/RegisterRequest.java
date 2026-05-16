package com.example.OperationSystem.dto;

import com.example.OperationSystem.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private Role role;
}
