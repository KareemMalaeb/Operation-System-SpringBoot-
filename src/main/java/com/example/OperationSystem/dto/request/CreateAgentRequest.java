package com.example.OperationSystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAgentRequest {
    @NotBlank(message = "Agent name is required")
    private String name;
    
    @NotBlank(message = "Agent Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    private String phone;
    private String address;
    @NotBlank(message = "Agent Source is required")
    private String source;
    private String companyName;
    
    @NotBlank(message = "Agent Country is required")
    private String country;
    
    private String specialization; // "SEA", "AIR", "SEA, AIR"
    
}
