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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.example.OperationSystem.dto.request.InvoiceRequest;
import com.example.OperationSystem.dto.response.InvoiceResponse;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.enums.InvoiceStatus;
import com.example.OperationSystem.service.InvoiceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;


    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices(
        @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(invoiceService.getAllInvoices(currentUser));
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @PostMapping("/invoices")
    public ResponseEntity<InvoiceResponse> createInvoice(
        @RequestBody InvoiceRequest request,
        @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(request, currentUser));
    }

    @PatchMapping("/invoices/{id}/status")
    public ResponseEntity<InvoiceResponse> updateInvoiceStatus(
            @PathVariable Long id,
            @RequestParam InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.updateStatus(id, status));
    }

    @DeleteMapping("/invoices/{id}")
    public ResponseEntity<Void> deleteInvoiceById(@PathVariable Long id) {
        invoiceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/invoices/{id}/export")
    public ResponseEntity<byte[]> exportInvoicePdf(@PathVariable Long id) {
        byte[] pdf = invoiceService.exportPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
