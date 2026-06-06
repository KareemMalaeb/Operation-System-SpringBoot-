package com.example.OperationSystem.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.OperationSystem.dto.request.UpdateProfileRequest;
import com.example.OperationSystem.dto.response.UpdateProfileResponse;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UpdateProfileResponse getProfile(User currentUser) {
        UpdateProfileResponse response = new UpdateProfileResponse();
        response.setId(currentUser.getId());
        response.setUsername(currentUser.getDisplayName());
        response.setEmail(currentUser.getEmail());
        response.setRole(currentUser.getRole().name());
        response.setMonthlyTarget(currentUser.getMonthlyTarget());
        response.setYearlyTarget(currentUser.getYearlyTarget());
        return response;
    }

    public UpdateProfileResponse updateProfile(User currentUser, UpdateProfileRequest request) {
        // ready for future additions — e.g. update name, email, etc.
        return getProfile(currentUser);
    }

    public void changePassword(User currentUser, UpdateProfileRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }
}