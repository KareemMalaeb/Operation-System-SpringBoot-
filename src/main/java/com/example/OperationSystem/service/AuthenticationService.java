package com.example.OperationSystem.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.OperationSystem.dto.request.LoginRequest;
import com.example.OperationSystem.dto.request.RegisterRequest;
import com.example.OperationSystem.dto.response.LoginResponse;
import com.example.OperationSystem.dto.response.RegisterResponse;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.repository.UserRepository;
import com.example.OperationSystem.security.JwtService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    
    public RegisterResponse register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        User saved = userRepository.save(user);
        return new RegisterResponse(saved.getId(), saved.getDisplayName(), request.getRole().name());
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.generateToken(request.getEmail());
        return new LoginResponse(token, user.getId(), user.getDisplayName(), user.getEmail(), user.getRole().name());
    }
}