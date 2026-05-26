package com.example.OperationSystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.OperationSystem.dto.response.UserResponse;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::form)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        User user =  userRepository.findById(id)
                
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return UserResponse.form(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
