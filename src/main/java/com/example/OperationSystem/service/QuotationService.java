package com.example.OperationSystem.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.OperationSystem.dto.request.AddQuotationRequest;
import com.example.OperationSystem.dto.request.SendToAgentsRequest;
import com.example.OperationSystem.dto.response.InquiryResponse;
import com.example.OperationSystem.entity.Agent;
import com.example.OperationSystem.entity.Inquiry;
import com.example.OperationSystem.entity.InquiryAgent;
import com.example.OperationSystem.entity.Quotation;
import com.example.OperationSystem.entity.StatusHistory;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.enums.AgentStatus;
import com.example.OperationSystem.enums.InquiryStatus;
import com.example.OperationSystem.exceptions.BusinessException;
import com.example.OperationSystem.exceptions.ResourceNotFoundException;
import com.example.OperationSystem.repository.AgentRepository;
import com.example.OperationSystem.repository.InquiryAgentRepository;
import com.example.OperationSystem.repository.InquiryRepository;
import com.example.OperationSystem.repository.QuotationRepository;
import com.example.OperationSystem.repository.StatusHistoryRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class QuotationService {

    private final InquiryRepository inquiryRepository;
    private final InquiryAgentRepository inquiryAgentRepository;
    private final AgentRepository agentRepository;
    private final EmailService emailService;
    private final StatusHistoryRepository statusHistoryRepository;
    private final QuotationRepository quotationRepository;

    // ____________________Send to Agents____________________
    
    public InquiryResponse sendToAgents(Long inquiryId, SendToAgentsRequest request, User currentUser) {
        Inquiry inquiry = findInquiry(inquiryId);

        for (Long agentId : request.getAgentIds()) {
            // Skip if this agent is already linked to this inquiry
            if (inquiryAgentRepository.existsByInquiryIdAndAgentId(inquiryId, agentId)) {
                continue;
            }

            Agent agent = agentRepository.findById(agentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Agent not found: " + agentId));

            // Save the link record
            InquiryAgent link = InquiryAgent.builder()
                    .inquiry(inquiry)
                    .agent(agent)
                    .status(AgentStatus.SENT)
                    .sentAt(LocalDateTime.now())
                    .build();
            inquiryAgentRepository.save(link);

            // Fire email in background thread — does not block the response
            emailService.SendAgentInquiryEmail(agent, inquiry);
        }

        // Update status to SENT_TO_AGENTS
        InquiryStatus prev = inquiry.getStatus();
        inquiry.setStatus(InquiryStatus.SENT_TO_AGENTS);
        inquiryRepository.save(inquiry);

        logHistory(inquiry, prev, InquiryStatus.SENT_TO_AGENTS, currentUser,
                "Sent to " + request.getAgentIds().size() + " agents");

        return InquiryResponse.from(inquiry);
    }

    //______________________Add Quotation______________________
    
    public InquiryResponse addQuotation(Long inquiryId, AddQuotationRequest request, User currentUser) {
        Inquiry inquiry = findInquiry(inquiryId);
        Agent agent = agentRepository.findById(request.getAgentId())
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found: " + request.getAgentId()));
        
        // Mark the inquiry-agent record as Replied
        inquiryAgentRepository.findByInquiry(inquiry).stream()
                .filter(ia -> ia.getAgent().getId().equals(request.getAgentId()))
                .findFirst()
                .ifPresent(ia -> {
                    ia.setStatus(AgentStatus.REPLIED);
                    ia.setRespondedAt(LocalDateTime.now());
                    inquiryAgentRepository.save(ia);
                });            
        
        Quotation quotation = Quotation.builder()
                .inquiry(inquiry)
                .agent(agent)
                .price(request.getPrice())
                .currency(request.getCurrency())
                .transitTime(request.getTransitTime())
                .remarks(request.getRemarks())
                .build();
            quotationRepository.save(quotation);                
       
        // Move status to RECEIVING_QUOTES
        InquiryStatus prev = inquiry.getStatus();
        inquiry.setStatus(InquiryStatus.RECEIVING_QUOTES);
        inquiryRepository.save(inquiry);

        logHistory(inquiry, prev, InquiryStatus.RECEIVING_QUOTES, currentUser,
                "Quotation received from " + agent.getName() + " — " + request.getPrice() + " " + request.getCurrency());

        return InquiryResponse.from(inquiry);        
        
    }       

    //______________________Select Quote______________________
    
    public InquiryResponse selectQuote(long inquiryId, Long quotationId, User currentUser) {
        Inquiry inquiry = findInquiry(inquiryId);

        List<Quotation> allQuotes = quotationRepository.findByInquiry(inquiry);
        if (allQuotes.isEmpty()) {
            throw new ResourceNotFoundException("No quotations found for this inquiry");
        }

        // Deselect all existing selections for this inquiry
        allQuotes.forEach(q -> {
            if (Boolean.TRUE.equals(q.getIsSelected())) {
                q.setIsSelected(false);
                quotationRepository.save(q);
            }
        });

        // Select the chosen quotation
        Quotation selected = allQuotes.stream()
                .filter(q -> q.getId().equals(quotationId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found with id: " + quotationId));
        selected.setIsSelected(true);
        quotationRepository.save(selected);

        // Move status to QUOTES_COMPLETED
        InquiryStatus prev = inquiry.getStatus();
        inquiry.setStatus(InquiryStatus.QUOTES_COMPLETED);
        inquiryRepository.save(inquiry);

        logHistory(inquiry, prev, InquiryStatus.QUOTES_COMPLETED, currentUser,
                "Quotation selected from agent: " + selected.getAgent().getName());

        return InquiryResponse.from(inquiry);
    }
    //_____________________Send to Client______________________
    
    public InquiryResponse sendToClient(Long id, User currentUser) {
        Inquiry inquiry = findInquiry(id);

        quotationRepository.findByInquiryAndIsSelectedTrue(inquiry)
                .orElseThrow(() -> new BusinessException("No quote selected. Select the best quote first."));

        InquiryStatus prev = inquiry.getStatus();
        inquiry.setStatus(InquiryStatus.QUOTED_TO_CLIENT);
        inquiryRepository.save(inquiry);

        logHistory(inquiry, prev, InquiryStatus.QUOTED_TO_CLIENT, currentUser,
                "Final quotation sent to client");

        emailService.sendClientQuotationEmail(inquiry);

        return InquiryResponse.from(inquiry);
    }
    //______________________Helper Methods______________________
    private Inquiry findInquiry(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found with id: " + id));
    
    }   
    private void logHistory(Inquiry inquiry, InquiryStatus from, InquiryStatus to,
                            User changedBy, String note) {
        statusHistoryRepository.save(StatusHistory.builder()
                .inquiry(inquiry)
                .fromStatus(from)
                .toStatus(to)
                .changedBy(changedBy)
                .note(note)
                .build());
    }  
}
