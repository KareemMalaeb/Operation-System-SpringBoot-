package com.example.OperationSystem.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.OperationSystem.dto.response.UserResponse;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.exceptions.BusinessException;
import com.example.OperationSystem.exceptions.ResourceNotFoundException;
import com.example.OperationSystem.repository.InquiryRepository;
import com.example.OperationSystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::form)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return UserResponse.form(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        boolean hasInquiries = !inquiryRepository.findByCreatedBy(user).isEmpty()
                || !inquiryRepository.findByAssignedTo(user).isEmpty();

        if (hasInquiries) {
            throw new BusinessException("Cannot delete this user — they have associated inquiries. Reassign or delete their inquiries first.");
        }

        userRepository.delete(user);
    }
}
