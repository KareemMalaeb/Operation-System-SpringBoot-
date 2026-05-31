package com.example.OperationSystem.dto.response;

import com.example.OperationSystem.entity.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String role;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getDisplayName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
