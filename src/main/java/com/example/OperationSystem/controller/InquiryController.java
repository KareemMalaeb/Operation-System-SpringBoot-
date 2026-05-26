package com.example.OperationSystem.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.OperationSystem.dto.request.AddQuotationRequest;
import com.example.OperationSystem.dto.request.AssignRequest;
import com.example.OperationSystem.dto.request.InquiryRequest;
import com.example.OperationSystem.dto.request.SelectQuoteRequest;
import com.example.OperationSystem.dto.request.SendToAgentsRequest;
import com.example.OperationSystem.dto.request.UpdateStatusRequest;
import com.example.OperationSystem.dto.response.InquiryResponse;
import com.example.OperationSystem.dto.response.InquirySummaryResponse;
import com.example.OperationSystem.dto.response.QuotationResponse;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.service.InquiryService;
import com.example.OperationSystem.service.QuotationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final QuotationService quotationService;

    @PostMapping("/inquiries")
    public ResponseEntity<InquiryResponse> createInquiry(
        @RequestBody InquiryRequest request,
        @AuthenticationPrincipal User currentUser) {
        InquiryResponse response = inquiryService.createInquiry(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/inquiries")
    public ResponseEntity<List<InquirySummaryResponse>> getInquiries(@AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(inquiryService.getInquiries(currentUser));
    }

    @GetMapping("/inquiries/{id}")
    public ResponseEntity<InquiryResponse> getInquiryById(@PathVariable Long id, @AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(inquiryService.getInquiryById(id, currentUser));
    }
    
    @DeleteMapping("/inquiries/{id}")
    public ResponseEntity<Void> deleteInquiryById(
        @PathVariable Long id,
        @AuthenticationPrincipal User currentUser) {
        inquiryService.deleteInquiryById(id, currentUser);
        return ResponseEntity.noContent().build();
    }   

    @PutMapping("/inquiries/{id}")
    public ResponseEntity<InquiryResponse> updateInquiry(
        @PathVariable Long id,
        @RequestBody InquiryRequest request,
        @AuthenticationPrincipal User currentUser) {
        InquiryResponse response = inquiryService.updateInquiry(id, request, currentUser);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/inquiries/{id}/assign")
    public ResponseEntity<InquiryResponse> assignInquiry(
        @PathVariable Long id,
        @RequestBody AssignRequest request,
        @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(inquiryService.assignInquiry(id, request, currentUser));
    }

    @PostMapping("/inquiries/{id}/agents/send")
    public ResponseEntity<InquiryResponse> sendToAgents(
        @PathVariable Long id,
        @Valid @RequestBody SendToAgentsRequest request,
        @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(quotationService.sendToAgents(id, request, currentUser));
    }

    @PatchMapping("/inquiries/{id}/status")
    public ResponseEntity<InquiryResponse> updateStatus(
        @PathVariable Long id,
        @RequestBody UpdateStatusRequest request,
        @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(inquiryService.updateStatus(id, request.getStatus(), request.getNote(), currentUser));
    }

    @PostMapping("/inquiries/{id}/quotations")
    public ResponseEntity<InquiryResponse> addQuotation(
        @PathVariable Long id,
        @RequestBody AddQuotationRequest request,
        @AuthenticationPrincipal User currentUser){
            return ResponseEntity.ok(quotationService.addQuotation(id, request, currentUser));
        }
    
    @GetMapping("/inquiries/{id}/quotations")
    public ResponseEntity<List<QuotationResponse>> getQuotations(
        @PathVariable Long id,
        @AuthenticationPrincipal User currentUser) {
            return ResponseEntity.ok(quotationService.getQuotations(id, currentUser));
        }

    @PatchMapping("/inquiries/{id}/quotations/select")
    public ResponseEntity<InquiryResponse> selectQuote(
        @PathVariable Long id,
        @Valid @RequestBody SelectQuoteRequest request,
        @AuthenticationPrincipal User currentUser) {
            return ResponseEntity.ok(quotationService.selectQuote(id, request.getQuotationId(), currentUser));
        }

    // sendToClient will fire the actual email to the client
    @PostMapping("/inquiries/{id}/send-to-client")
    public ResponseEntity<InquiryResponse> sendToClient(
        @PathVariable Long id,
        @AuthenticationPrincipal User currentUser) {
            return ResponseEntity.ok(quotationService.sendToClient(id, currentUser));
    }

    @PostMapping("/inquiries/{id}/documents")
    public ResponseEntity<InquiryResponse> uploadDocuments(
        @PathVariable Long id,
        @RequestParam (required = false) MultipartFile plFile,
        @RequestParam (required = false)MultipartFile ciFile,
        @AuthenticationPrincipal User currentUser) {
            return ResponseEntity.ok(inquiryService.uploadDocuments(id, plFile, ciFile, currentUser));

    }
}

 