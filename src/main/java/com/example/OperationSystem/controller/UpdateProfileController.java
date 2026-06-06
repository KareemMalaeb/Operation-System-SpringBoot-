package com.example.OperationSystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.OperationSystem.dto.request.UpdateProfileRequest;
import com.example.OperationSystem.dto.response.UpdateProfileResponse;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.service.UpdateProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UpdateProfileController {

    private final UpdateProfileService updateProfileService;

    @GetMapping("/profile")
    public ResponseEntity<UpdateProfileResponse> getProfile(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(updateProfileService.getProfile(currentUser));
    }

    @PatchMapping("/profile")
    public ResponseEntity<UpdateProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(updateProfileService.updateProfile(currentUser, request));
    }

    @PatchMapping("/profile/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal User currentUser) {
        updateProfileService.changePassword(currentUser, request);
        return ResponseEntity.noContent().build();
    }
}
