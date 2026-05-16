package com.example.OperationSystem.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.OperationSystem.dto.LoginRequest;
import com.example.OperationSystem.dto.LoginResponse;
import com.example.OperationSystem.dto.RegisterRequest;
import com.example.OperationSystem.dto.RegisterResponse;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    
    public RegisterResponse register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        User saved = userRepository.save(user);
        return new RegisterResponse(saved.getId(), saved.getUsername(), request.getRole().name());
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        return new LoginResponse(jwtService.generateToken(request.getUsername()));
    }
}