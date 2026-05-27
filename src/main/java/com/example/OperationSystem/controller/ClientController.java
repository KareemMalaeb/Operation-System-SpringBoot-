package com.example.OperationSystem.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.example.OperationSystem.dto.request.AddClientRequest;
import com.example.OperationSystem.dto.response.AddClientResponse;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.service.ClientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    @PostMapping("/clients")
    public ResponseEntity<AddClientResponse> addClient(
            @RequestBody AddClientRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.addClient(request, currentUser));
    }

    @GetMapping("/clients")
    public ResponseEntity<List<AddClientResponse>> getAllClients(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(clientService.getAllClients(currentUser));
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<AddClientResponse> getClientById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(clientService.getClientById(id, currentUser));
    }

    @PutMapping("/clients/{id}")
    public ResponseEntity<AddClientResponse> updateClientById(
        @PathVariable long id, 
        @RequestBody AddClientRequest request,
        @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(clientService.updateClientById(id, request, currentUser));
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteClientById(
        @PathVariable long id,
        @AuthenticationPrincipal User currentUser){
        clientService.deleteClientById(id, currentUser);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/clients/report")
    public ResponseEntity<byte[]> exportReport(
            @AuthenticationPrincipal User currentUser) {
        byte[] pdf = clientService.exportReport(currentUser);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "clients-report.pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }


     

}