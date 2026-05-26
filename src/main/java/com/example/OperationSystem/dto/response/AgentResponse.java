package com.example.OperationSystem.dto.response;

import com.example.OperationSystem.entity.Agent;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String Source;
    private String CompanyName;
    private String Country;

    public static AgentResponse from(Agent agent){
        return AgentResponse.builder()
            .id(agent.getId())
            .name(agent.getName())
            .email(agent.getEmail())
            .phone(agent.getPhone())
            .address(agent.getAddress())
            .Source(agent.getSource())
            .CompanyName(agent.getCompanyName())
            .Country(agent.getCountry())
            .build();
    }
}
