package com.example.OperationSystem.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.OperationSystem.entity.Agent;
import com.example.OperationSystem.entity.Inquiry;

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
        context.setVariable("freightType", inquiry.getFreightType());
        context.setVariable("containerType", inquiry.getContainerType());
        context.setVariable("details", inquiry.getAdditionalDetails());

        String html = templateEngine.process("agent-inquiry", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(agent.getEmail());
            helper.setSubject("Freight Inquiry Request - " + inquiry.getCode());
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Email sent to agent {} for inquiry {}", agent.getEmail(), inquiry.getCode());

        } catch (MessagingException e) {
            log.error("Failed to send email to agent {}: {}", agent.getEmail(), e.getMessage());
        }
    }
    
    
    @Async
    public void sendClientQuotationEmail(Inquiry inquiry) {
        Context context = new Context();
        context.setVariable("clientName", inquiry.getClientName());
        context.setVariable("inquiryCode", inquiry.getCode());
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