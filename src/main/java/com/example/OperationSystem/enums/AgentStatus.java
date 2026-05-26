package com.example.OperationSystem.enums;

public enum AgentStatus {
    PENDING,   // Selected but email not sent yet
    SENT,      // Email sent, waiting for reply
    REPLIED    // Agent responded with a quotation    
}
