package com.example.OperationSystem.dto.response;

import com.example.OperationSystem.entity.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class UserResponse {
    
    private Long id;
    private String username;
    private String role;

    public static UserResponse form(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}
