package com.example.OperationSystem.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.OperationSystem.dto.request.CreateAgentRequest;
import com.example.OperationSystem.dto.response.AgentResponse;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.service.AgentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AgentController {
    private final AgentService agentService;

    @PostMapping("/agents")
    public ResponseEntity<AgentResponse> createAgent(
        @Valid @RequestBody CreateAgentRequest request,
        @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agentService.createAgent(request, currentUser));    
    }
    
    @GetMapping("/agents")
    public ResponseEntity<List<AgentResponse>> getAllAgents(
        @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(agentService.getAllAgents(currentUser));
    }

    @GetMapping("/agents/{id}")
    public ResponseEntity<AgentResponse> getAgentById(
        @PathVariable Long id,
        @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(agentService.getAgentById(id, currentUser));
    }

    @DeleteMapping("/agents/{id}")
    public ResponseEntity<Void> deleteAgentById(
        @PathVariable Long id,
        @AuthenticationPrincipal User currentUser) {
        agentService.deleteAgentById(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/agents/{id}")
    public ResponseEntity<AgentResponse> updateAgentById(
        @PathVariable Long id,
        @Valid @RequestBody CreateAgentRequest request,
        @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(agentService.updateAgent(id, request, currentUser));
    }
    
}

