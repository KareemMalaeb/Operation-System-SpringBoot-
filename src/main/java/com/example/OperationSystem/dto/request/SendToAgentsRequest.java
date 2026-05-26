package com.example.OperationSystem.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SendToAgentsRequest {
    @NotEmpty(message = "At least one agent must be selected")
    private List<Long> agentIds;    
}
