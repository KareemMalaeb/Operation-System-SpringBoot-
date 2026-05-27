package com.example.OperationSystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.OperationSystem.dto.request.AddQuotationRequest;
import com.example.OperationSystem.dto.request.SelectQuoteRequest;
import com.example.OperationSystem.dto.response.InquiryResponse;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.service.QuotationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;

    @PostMapping("/inquiries/{id}/quotations")
    public ResponseEntity<InquiryResponse> addQuotation(
            @PathVariable Long id,
            @RequestBody AddQuotationRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(quotationService.addQuotation(id, request, currentUser));
    }

    @PatchMapping("/inquiries/{id}/quotations/select")
    public ResponseEntity<InquiryResponse> selectQuote(
            @PathVariable Long id,
            @Valid @RequestBody SelectQuoteRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(quotationService.selectQuote(id, request.getQuotationId(), request.getSellingPrice(), currentUser));
    }
}