package com.example.OperationSystem.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.OperationSystem.dto.request.AssignRequest;
import com.example.OperationSystem.dto.request.SendToAgentsRequest;
import com.example.OperationSystem.dto.request.InquiryRequest;
import com.example.OperationSystem.dto.response.InquiryResponse;
import com.example.OperationSystem.dto.response.InquirySummaryResponse;
import com.example.OperationSystem.entity.Agent;
import com.example.OperationSystem.entity.Inquiry;
import com.example.OperationSystem.entity.InquiryAgent;
import com.example.OperationSystem.entity.StatusHistory;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.enums.AgentStatus;
import com.example.OperationSystem.enums.InquiryStatus;
import com.example.OperationSystem.enums.Role;
import com.example.OperationSystem.exceptions.ResourceNotFoundException;
import com.example.OperationSystem.repository.AgentRepository;
import com.example.OperationSystem.repository.InquiryAgentRepository;
import com.example.OperationSystem.repository.InquiryRepository;
import com.example.OperationSystem.repository.InvoiceRepository;
import com.example.OperationSystem.repository.QuotationRepository;
import com.example.OperationSystem.repository.StatusHistoryRepository;
import com.example.OperationSystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;
    private final InquiryAgentRepository inquiryAgentRepository;
    private final QuotationRepository quotationRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;
    private final AgentRepository agentRepository;
    private final EmailService emailService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public InquiryResponse createInquiry(InquiryRequest request, User currentUser) {
        String code = generateCode(currentUser);
        Inquiry inquiry = Inquiry.builder()
                .code(code)
                .clientName(request.getClientName())
                .clientPhone(request.getClientPhone())
                .clientEmail(request.getClientEmail())
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .encoterms(request.getEncoterms())
                .dg(request.getDg())
                .freightType(request.getFreightType())
                .containerType(request.getContainerType())
                .additionalDetails(request.getAdditionalDetails())
                .supplierLocation(request.getSupplierLocation())
                .createdBy(currentUser)
                .status(InquiryStatus.NEW)
                .build();
        Inquiry saved = inquiryRepository.save(inquiry);
        logHistory(saved, null, InquiryStatus.NEW, currentUser,
                "Inquiry created by " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");

        InquiryResponse response = InquiryResponse.from(saved);
        return response;
    }

    public List<InquirySummaryResponse> getInquiries(User currentUser) {
        List<Inquiry> inquiries = switch (currentUser.getRole()) {
            case SALES      -> inquiryRepository.findByCreatedBy(currentUser);
            case OPERATOR -> inquiryRepository.findByAssignedTo(currentUser);
            case MANAGER    -> inquiryRepository.findAll();
        };
        return inquiries.stream()
                .map(InquirySummaryResponse::from)
                .collect(Collectors.toList());
    }

    public InquiryResponse getInquiryById(Long id, User currentUser) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));

        boolean allowed = switch (currentUser.getRole()) {
            case SALES      -> inquiry.getCreatedBy() != null && inquiry.getCreatedBy().getId().equals(currentUser.getId());
            case OPERATOR -> inquiry.getAssignedTo() != null && inquiry.getAssignedTo().getId().equals(currentUser.getId());
            case MANAGER    -> true;
        };

        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return InquiryResponse.from(inquiry);
    }

    @Transactional
    public InquiryResponse updateInquiry(Long id, InquiryRequest request, User currentUser) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));

        boolean allowed = switch (currentUser.getRole()) {
            case SALES      -> inquiry.getCreatedBy() != null && inquiry.getCreatedBy().getId().equals(currentUser.getId());
            case OPERATOR -> false;
            case MANAGER    -> true;
        };

        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        inquiry.setClientName(request.getClientName());
        inquiry.setClientPhone(request.getClientPhone());
        inquiry.setClientEmail(request.getClientEmail());
        inquiry.setOrigin(request.getOrigin());
        inquiry.setDestination(request.getDestination());
        inquiry.setFreightType(request.getFreightType());
        inquiry.setContainerType(request.getContainerType());
        inquiry.setAdditionalDetails(request.getAdditionalDetails());
        inquiry.setSupplierLocation(request.getSupplierLocation());
        inquiry.setDg(request.getDg());

        Inquiry saved = inquiryRepository.save(inquiry);
        return InquiryResponse.from(saved);
    }

    @Transactional
    public InquiryResponse assignInquiry(Long id, AssignRequest request, User currentUser) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));

        boolean canAssign = switch (currentUser.getRole()) {
            case MANAGER  -> true;
            case SALES    -> inquiry.getCreatedBy() != null
                             && inquiry.getCreatedBy().getId().equals(currentUser.getId());
            case OPERATOR -> false;
        };
        if (!canAssign) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        User operator = userRepository.findById(request.getOperatorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operator not found"));

        if (operator.getRole() != Role.OPERATOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only assign to OPERATIONS users");
        }

        InquiryStatus prev = inquiry.getStatus();
        inquiry.setAssignedTo(operator);
        inquiry.setStatus(InquiryStatus.ASSIGNED);
        Inquiry saved = inquiryRepository.save(inquiry);

        logHistory(saved, prev, InquiryStatus.ASSIGNED, currentUser,
                "Assigned to " + operator.getUsername() + " (Operator)");

        notificationService.createNotification(
                operator,
                "Inquiry " + saved.getCode() + " has been assigned to you",
                saved.getId()
        );

        return InquiryResponse.from(saved);
    }

    @Transactional
    public InquiryResponse updateStatus(Long id, InquiryStatus newStatus, String note, User currentUser) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));

        InquiryStatus prev = inquiry.getStatus();
        inquiry.setStatus(newStatus);
        Inquiry saved = inquiryRepository.save(inquiry);
        logHistory(saved, prev, newStatus, currentUser, note != null ? note : "Status updated to " + newStatus);
        return InquiryResponse.from(saved);
    }

    @Transactional
    public void deleteInquiryById(Long id, User currentUser) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));

        boolean allowed = switch (currentUser.getRole()) {
            case SALES      -> inquiry.getCreatedBy() != null && inquiry.getCreatedBy().getId().equals(currentUser.getId());
            case OPERATOR -> false;
            case MANAGER    -> true;
        };

        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        invoiceRepository.deleteByInquiry(inquiry);
        inquiryAgentRepository.deleteByInquiry(inquiry);
        quotationRepository.deleteByInquiry(inquiry);
        statusHistoryRepository.deleteByInquiry(inquiry);
        inquiryRepository.delete(inquiry);
    }

    private String generateCode(User salesUser) {
        String initial = String.valueOf(salesUser.getUsername().charAt(0)).toUpperCase();
        String prefix = "Z" + initial;

        List<String> existing = inquiryRepository.findCodesByPrefix(prefix);

        int max = existing.stream()
                .map(code -> code.split("-")[1])
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        return prefix + "-" + String.format("%03d", max + 1);
    }

    private void logHistory(Inquiry inquiry, InquiryStatus from, InquiryStatus to,
                            User changedBy, String note) {
        StatusHistory history = StatusHistory.builder()
                .inquiry(inquiry)
                .fromStatus(from)
                .toStatus(to)
                .changedBy(changedBy)
                .note(note)
                .build();
        statusHistoryRepository.save(history);
    }

    @Transactional
    public InquiryResponse uploadDocuments(Long id, MultipartFile plFile, MultipartFile ciFile, User currentUser) {
        // check inquiry exists 
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));
        // check permissions
        boolean allowed = switch (currentUser.getRole()) {
            case SALES    -> inquiry.getCreatedBy() != null && inquiry.getCreatedBy().getId().equals(currentUser.getId());
            case OPERATOR -> inquiry.getAssignedTo() != null && inquiry.getAssignedTo().getId().equals(currentUser.getId());
            case MANAGER  -> true;
        };
        // if no permissions, throw 403
        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        // save files and update paths in inquiry
        if (plFile != null && !plFile.isEmpty()) {
            inquiry.setPlFilePath(saveFile(plFile, id, "PL"));
        } 
          
        if (ciFile != null && !ciFile.isEmpty()) {
            inquiry.setCiFilePath(saveFile(ciFile, id, "CI"));
        }
        // save inquiry
        return InquiryResponse.from(inquiryRepository.save(inquiry));
    }

    public InquiryResponse sendToAgents(Long inquiryId, SendToAgentsRequest request, User currentUser) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));

        for (Long agentId : request.getAgentIds()) {
            if (inquiryAgentRepository.existsByInquiryIdAndAgentId(inquiryId, agentId)) {
                continue;
            }
            Agent agent = agentRepository.findById(agentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Agent not found: " + agentId));
            InquiryAgent link = InquiryAgent.builder()
                    .inquiry(inquiry)
                    .agent(agent)
                    .status(AgentStatus.SENT)
                    .sentAt(LocalDateTime.now())
                    .build();
            inquiryAgentRepository.save(link);
            emailService.SendAgentInquiryEmail(agent, inquiry);
        }

        InquiryStatus prev = inquiry.getStatus();
        inquiry.setStatus(InquiryStatus.SENT_TO_AGENTS);
        inquiryRepository.save(inquiry);
        logHistory(inquiry, prev, InquiryStatus.SENT_TO_AGENTS, currentUser,
                "Sent to " + request.getAgentIds().size() + " agents");
        notificationService.createNotification(
                inquiry.getCreatedBy(),
                "Inquiry " + inquiry.getCode() + " has been sent to agents",
                inquiry.getId()
        );
        return InquiryResponse.from(inquiry);
    }
    // helper method to save file and return path
    private String saveFile(MultipartFile file, Long inquiryId, String prefix) {
        try {
            Path dir = Paths.get(uploadDir, "inquiries", inquiryId.toString());
            Files.createDirectories(dir);
            String filename = prefix + "_" + file.getOriginalFilename();
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save file");
        }
    }


}
