package com.example.OperationSystem.enums;

public enum InquiryStatus {
    NEW,               // Just created by Sales
    ASSIGNED,          // Assigned to an Operation user
    SENT_TO_AGENTS,    // Emails sent to agents
    RECEIVING_QUOTES,  // At least one agent replied
    QUOTES_COMPLETED,  // All agents replied
    QUOTED_TO_CLIENT,  // Best quote sent to client
    WON,               // Client accepted
    LOST               // Client declined
}
