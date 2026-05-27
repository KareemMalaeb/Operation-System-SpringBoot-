package com.example.OperationSystem.dto.request;

import lombok.Data;

@Data

public class AddClientRequest {
    
    private String companyName;
    private String industry;
    private String contactName;
    private String contactNumber;
    private String email;
    private String phone;
    private String notes;
}
