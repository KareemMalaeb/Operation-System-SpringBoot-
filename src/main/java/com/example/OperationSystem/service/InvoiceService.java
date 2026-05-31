package com.example.OperationSystem.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import com.example.OperationSystem.dto.request.InvoiceRequest;
import com.example.OperationSystem.dto.response.InvoiceResponse;
import com.example.OperationSystem.entity.Inquiry;
import com.example.OperationSystem.entity.Invoice;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.enums.InvoiceStatus;
import com.example.OperationSystem.repository.InquiryRepository;
import com.example.OperationSystem.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InquiryRepository inquiryRepository;

    public List<InvoiceResponse> getAllInvoices(User currentUser) {
        return invoiceRepository.findAll()
                .stream()
                .map(InvoiceResponse::from)
                .toList();
    }
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        return InvoiceResponse.from(invoice);
    }

    public InvoiceResponse createInvoice(InvoiceRequest request, User currentUser) {
        Inquiry inquiry = inquiryRepository.findById(request.getInquiryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));

        BigDecimal amount    = request.getAmount() != null ? request.getAmount() : inquiry.getSellingPrice();
        BigDecimal taxRate   = request.getTaxRate() != null ? request.getTaxRate() : BigDecimal.ZERO;
        BigDecimal taxAmount = amount != null ? amount.multiply(taxRate).divide(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
        BigDecimal total     = amount != null ? amount.add(taxAmount) : BigDecimal.ZERO;

        Invoice saved = Invoice.builder()
                .invoiceNumber(generateInvoiceNumber())
                .inquiry(inquiry)
                .clientName(inquiry.getClientName())
                .clientEmail(inquiry.getClientEmail())
                .issueDate(request.getIssueDate() != null ? request.getIssueDate() : LocalDate.now())
                .dueDate(request.getDueDate() != null ? request.getDueDate() : LocalDate.now().plusDays(30))
                .currency(request.getCurrency() != null ? request.getCurrency() : inquiry.getSellingCurrency())
                .amount(amount)
                .taxRate(taxRate)
                .taxAmount(taxAmount)
                .totalAmount(total)
                .notes(request.getNotes())
                .status(InvoiceStatus.DRAFT)
                .createdAt(LocalDate.now())
                .build();

        return InvoiceResponse.from(invoiceRepository.save(saved));
    }
    
    private String generateInvoiceNumber() {
        int year = LocalDate.now().getYear();
        long next = invoiceRepository.count() + 1;
        return String.format("INV-%d-%03d", year, next);
    }

    public InvoiceResponse updateStatus(Long id, InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        invoice.setStatus(status);
        return InvoiceResponse.from(invoiceRepository.save(invoice));
    }
    // helper method to delete invoice by id
    public void deleteById(Long id) {
        invoiceRepository.deleteById(id);
    }
    // helper method to generate pdf
    public byte[] exportPdf(Long id) {
        Invoice inv = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font labelFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            Font valueFont  = FontFactory.getFont(FontFactory.HELVETICA, 9);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font cellFont   = FontFactory.getFont(FontFactory.HELVETICA, 9);
            Font totalFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

            // ── Header ──────────────────────────────────────────────
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{3f, 2f});
            header.setSpacingAfter(16);

            PdfPCell titleCell = new PdfPCell(new Phrase("INVOICE", titleFont));
            titleCell.setBorder(0);
            titleCell.setPaddingBottom(4);
            header.addCell(titleCell);

            String invNum = inv.getInvoiceNumber() != null ? inv.getInvoiceNumber() : "-";
            String details = "Invoice #:  " + invNum + "\n"
                    + "Issue Date: " + (inv.getIssueDate() != null ? inv.getIssueDate() : "-") + "\n"
                    + "Due Date:   " + (inv.getDueDate()   != null ? inv.getDueDate()   : "-") + "\n"
                    + "Status:     " + (inv.getStatus() != null ? inv.getStatus().name() : "-");
            PdfPCell detailCell = new PdfPCell(new Phrase(details, valueFont));
            detailCell.setBorder(0);
            detailCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            header.addCell(detailCell);
            doc.add(header);

            // ── Bill To ──────────────────────────────────────────────
            PdfPTable billTo = new PdfPTable(2);
            billTo.setWidthPercentage(100);
            billTo.setWidths(new float[]{1f, 2f});
            billTo.setSpacingAfter(16);

            for (String[] row : new String[][]{
                    {"Bill To", inv.getClientName() != null ? inv.getClientName() : "-"},
                    {"Email",   inv.getClientEmail() != null ? inv.getClientEmail() : "-"},
                    {"Inquiry", inv.getInquiry() != null ? inv.getInquiry().getCode() : "-"}
            }) {
                PdfPCell lbl = new PdfPCell(new Phrase(row[0], labelFont));
                lbl.setBorder(0);
                lbl.setPadding(3);
                PdfPCell val = new PdfPCell(new Phrase(row[1], valueFont));
                val.setBorder(0);
                val.setPadding(3);
                billTo.addCell(lbl);
                billTo.addCell(val);
            }
            doc.add(billTo);

            // ── Line Items ───────────────────────────────────────────
            PdfPTable items = new PdfPTable(4);
            items.setWidthPercentage(100);
            items.setWidths(new float[]{4f, 2f, 2f, 2f});
            items.setSpacingAfter(8);

            for (String h : new String[]{"Description", "Amount", "Tax", "Total"}) {
                PdfPCell c = new PdfPCell(new Phrase(h, headerFont));
                c.setBackgroundColor(new Color(29, 78, 216));
                c.setPadding(7);
                items.addCell(c);
            }

            String currency = inv.getCurrency() != null ? inv.getCurrency() + " " : "";
            String taxLabel = inv.getTaxRate() != null ? inv.getTaxRate().stripTrailingZeros().toPlainString() + "%" : "0%";

            items.addCell(dataCell("Freight Charge", cellFont));
            items.addCell(dataCell(currency + fmt(inv.getAmount()), cellFont));
            items.addCell(dataCell(taxLabel + "  (" + currency + fmt(inv.getTaxAmount()) + ")", cellFont));
            items.addCell(dataCell(currency + fmt(inv.getTotalAmount()), cellFont));
            doc.add(items);

            // ── Total row ────────────────────────────────────────────
            PdfPTable totals = new PdfPTable(2);
            totals.setWidthPercentage(40);
            totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totals.setSpacingAfter(16);

            PdfPCell totalLabel = new PdfPCell(new Phrase("Total Due", totalFont));
            totalLabel.setBackgroundColor(new Color(241, 245, 249));
            totalLabel.setPadding(7);
            PdfPCell totalValue = new PdfPCell(new Phrase(currency + fmt(inv.getTotalAmount()), totalFont));
            totalValue.setPadding(7);
            totals.addCell(totalLabel);
            totals.addCell(totalValue);
            doc.add(totals);

            // ── Notes ────────────────────────────────────────────────
            if (inv.getNotes() != null && !inv.getNotes().isBlank()) {
                Paragraph notesLabel = new Paragraph("Notes", labelFont);
                notesLabel.setSpacingBefore(8);
                doc.add(notesLabel);
                doc.add(new Paragraph(inv.getNotes(), valueFont));
            }

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate invoice PDF");
        }
    }

    private PdfPCell dataCell(String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text != null ? text : "-", font));
        c.setPadding(6);
        return c;
    }

    private String fmt(BigDecimal value) {
        return value != null ? value.stripTrailingZeros().toPlainString() : "-";
    }

}
