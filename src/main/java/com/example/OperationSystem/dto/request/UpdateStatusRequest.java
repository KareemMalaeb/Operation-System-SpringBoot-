package com.example.OperationSystem.dto.request;

import com.example.OperationSystem.enums.InquiryStatus;

import lombok.Data;
@Data
public class UpdateStatusRequest {
    private InquiryStatus Status;
    private String note;
    
}
