package com.example.OperationSystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.OperationSystem.entity.User;

import com.example.OperationSystem.dto.request.LoginRequest;
import com.example.OperationSystem.dto.request.RegisterRequest;
import com.example.OperationSystem.dto.response.LoginResponse;
import com.example.OperationSystem.dto.response.RegisterResponse;
import com.example.OperationSystem.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/api/auth/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authenticationService.login(request));
    }
    
    // Read the existing token and returns the user information
    @GetMapping("/api/auth/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(java.util.Map.of(
            "id", currentUser.getId(),
            "name", currentUser.getDisplayName(),
            "email", currentUser.getEmail(),
            "role", currentUser.getRole().name()
        ));
    }
}

