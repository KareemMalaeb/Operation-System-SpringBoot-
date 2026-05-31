package com.example.OperationSystem.service;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.OperationSystem.entity.Agent;
import com.example.OperationSystem.entity.Inquiry;
import com.example.OperationSystem.entity.Quotation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Async
    public void SendAgentInquiryEmail(Agent agent, Inquiry inquiry) {
        Context context = new Context();
        context.setVariable("inquiryCode", inquiry.getCode());
        context.setVariable("agentName", agent.getName());
        context.setVariable("origin", inquiry.getOrigin());
        context.setVariable("destination", inquiry.getDestination());
        context.setVariable("freightType", inquiry.getFreightType() != null ? inquiry.getFreightType().getLabel() : null);
        context.setVariable("containerType", inquiry.getContainerType() != null ? inquiry.getContainerType().getLabel() : null);
        context.setVariable("encoterms", inquiry.getEncoterms() != null ? inquiry.getEncoterms().getLabel() : null);
        context.setVariable("supplierLocation", inquiry.getSupplierLocation());
        context.setVariable("dg", inquiry.getDg() != null ? inquiry.getDg().getLabel() : null);
        context.setVariable("details", inquiry.getAdditionalDetails());

        String html = templateEngine.process("agent-inquiry", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(agent.getEmail());
            helper.setSubject("Freight Inquiry Request - " + inquiry.getCode());
            helper.setText(html, true);

            attachFile(helper, inquiry.getPlFilePath(), "PL");
            attachFile(helper, inquiry.getCiFilePath(), "CI");

            mailSender.send(message);
            log.info("Email sent to agent {} for inquiry {}", agent.getEmail(), inquiry.getCode());

        } catch (MessagingException e) {
            log.error("Failed to send email to agent {}: {}", agent.getEmail(), e.getMessage());
        }
    }

    private void attachFile(MimeMessageHelper helper, String filePath, String label) throws MessagingException {
        if (filePath == null || filePath.isBlank()) return;
        File file = new File(filePath);
        if (!file.exists()) {
            log.warn("{} file not found at path: {}", label, filePath);
            return;
        }
        helper.addAttachment(file.getName(), new FileSystemResource(file));
    }
    
    
    @Async
    public void sendClientQuotationEmail(Inquiry inquiry, Quotation quotation) {
        Context context = new Context();
        context.setVariable("clientName", inquiry.getClientName());
        context.setVariable("inquiryCode", inquiry.getCode());
        context.setVariable("origin", inquiry.getOrigin());
        context.setVariable("destination", inquiry.getDestination());
        context.setVariable("freightType", inquiry.getFreightType() != null ? inquiry.getFreightType().getLabel() : null);
        context.setVariable("containerType", inquiry.getContainerType() != null ? inquiry.getContainerType().getLabel() : null);
        context.setVariable("sellingPrice", inquiry.getSellingPrice());
        context.setVariable("currency", inquiry.getSellingCurrency());
        context.setVariable("transitTime", quotation.getTransitTime());
        context.setVariable("remarks", inquiry.getClientOfferNotes() != null ? inquiry.getClientOfferNotes() : quotation.getRemarks());
        String html = templateEngine.process("client-quotation", context);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(inquiry.getClientEmail());
            helper.setSubject("Your Freight Quotation is Ready - " + inquiry.getCode());
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Quotation email sent to client {} for inquiry {}", inquiry.getClientEmail(), inquiry.getCode());
        } catch (MessagingException e) {
            log.error("Failed to send quotation email to client {}: {}", inquiry.getClientEmail(), e.getMessage());
        }
    }
}