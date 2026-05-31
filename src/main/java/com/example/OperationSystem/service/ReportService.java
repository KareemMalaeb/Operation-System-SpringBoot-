package com.example.OperationSystem.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.OperationSystem.entity.Inquiry;
import com.example.OperationSystem.entity.Quotation;
import com.example.OperationSystem.enums.InquiryStatus;
import com.example.OperationSystem.exceptions.BusinessException;
import com.example.OperationSystem.repository.InquiryRepository;
import com.example.OperationSystem.repository.QuotationRepository;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final InquiryRepository inquiryRepository;
    private final QuotationRepository quotationRepository;

    public byte[] exportReport(String startDate, String endDate, Long salesId) {
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end   = LocalDate.parse(endDate).atTime(23, 59, 59);

        List<Inquiry> inquiries = inquiryRepository.findForReport(start, end, salesId);

        long total      = inquiries.size();
        long won        = inquiries.stream().filter(i -> i.getStatus() == InquiryStatus.WON).count();
        long lost       = inquiries.stream().filter(i -> i.getStatus() == InquiryStatus.LOST).count();
        long inProgress = total - won - lost;
        double winRate  = total > 0 ? (won * 100.0 / total) : 0;

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font subFont    = FontFactory.getFont(FontFactory.HELVETICA, 8);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, Color.WHITE);
            Font cellFont   = FontFactory.getFont(FontFactory.HELVETICA, 7);
            Font boldCell   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);

            document.add(new Paragraph("Inquiries Report", titleFont));
            document.add(new Paragraph("Period: " + startDate + " to " + endDate, subFont));
            document.add(new Paragraph(" "));

            // Summary stats row
            PdfPTable stats = new PdfPTable(8);
            stats.setWidthPercentage(100);
            for (String[] s : new String[][]{
                    {"Total", String.valueOf(total)},
                    {"Won", String.valueOf(won)},
                    {"Lost", String.valueOf(lost)},
                    {"In Progress", String.valueOf(inProgress)}
            }) {
                PdfPCell label = new PdfPCell(new Phrase(s[0], subFont));
                label.setBackgroundColor(new Color(241, 245, 249));
                label.setPadding(6);
                label.setBorderColor(new Color(226, 232, 240));

                PdfPCell value = new PdfPCell(new Phrase(s[1], boldCell));
                value.setPadding(6);
                value.setBorderColor(new Color(226, 232, 240));

                stats.addCell(label);
                stats.addCell(value);
            }
            document.add(stats);
            document.add(new Paragraph("Win Rate: " + String.format("%.1f", winRate) + "%", subFont));
            document.add(new Paragraph(" "));

            // Inquiry table — 9 columns
            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.0f, 1.5f, 1.2f, 1.2f, 1.2f, 1.5f, 1.5f, 1.2f, 3.5f});

            for (String header : new String[]{"Code", "Client", "Origin", "Destination", "Status",
                    "Buying Rate", "Selling Rate", "Profit", "Notes"}) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(29, 78, 216));
                cell.setPadding(6);
                table.addCell(cell);
            }

            for (Inquiry i : inquiries) {
                Optional<Quotation> selectedQuote = quotationRepository.findByInquiryAndIsSelectedTrue(i);

                BigDecimal buyingPrice  = selectedQuote.map(Quotation::getPrice).orElse(null);
                String     buyCurrency  = selectedQuote.map(Quotation::getCurrency).orElse("");
                BigDecimal sellingPrice = i.getSellingPrice();
                String     sellCurrency = i.getSellingCurrency() != null ? i.getSellingCurrency() : "";

                BigDecimal profit = (buyingPrice != null && sellingPrice != null)
                        ? sellingPrice.subtract(buyingPrice)
                        : null;

                table.addCell(cell(i.getCode(), cellFont));
                table.addCell(cell(i.getClientName(), cellFont));
                table.addCell(cell(i.getOrigin(), cellFont));
                table.addCell(cell(i.getDestination(), cellFont));
                table.addCell(cell(i.getStatus() != null ? i.getStatus().name() : "-", cellFont));
                table.addCell(cell(buyingPrice != null ? buyCurrency + " " + buyingPrice : "-", cellFont));
                table.addCell(cell(sellingPrice != null ? sellCurrency + " " + sellingPrice : "-", cellFont));

                PdfPCell profitCell = new PdfPCell(new Phrase(
                        profit != null ? sellCurrency + " " + profit : "-", cellFont));
                profitCell.setPadding(5);
                if (profit != null && profit.compareTo(BigDecimal.ZERO) > 0) {
                    profitCell.setBackgroundColor(new Color(220, 252, 231));
                } else if (profit != null && profit.compareTo(BigDecimal.ZERO) < 0) {
                    profitCell.setBackgroundColor(new Color(254, 226, 226));
                }
                table.addCell(profitCell);

                PdfPCell notesCell = new PdfPCell(new Phrase(
                        i.getClientOfferNotes() != null ? i.getClientOfferNotes() : "-", cellFont));
                notesCell.setPadding(5);
                notesCell.setNoWrap(false);
                table.addCell(notesCell);
            }

            document.add(table);
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new BusinessException("Failed to generate report");
        }
    }

    private PdfPCell cell(String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text != null ? text : "-", font));
        c.setPadding(5);
        return c;
    }
}