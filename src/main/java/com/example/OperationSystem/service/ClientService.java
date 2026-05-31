package com.example.OperationSystem.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import java.awt.Color;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.OperationSystem.dto.request.AddClientRequest;
import com.example.OperationSystem.dto.response.AddClientResponse;
import com.example.OperationSystem.entity.Client;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.exceptions.BusinessException;
import com.example.OperationSystem.repository.ClientRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    @CacheEvict(value = "clients", key = "#currentUser.id")
    public AddClientResponse addClient(AddClientRequest request, User currentUser) {
        if (clientRepository.existsByCompanyName(request.getCompanyName())) {
            throw new BusinessException("Client with company name " + request.getCompanyName() + " already exists");
        }
        Client client = Client.builder()
                .companyName(request.getCompanyName())
                .industry(request.getIndustry()) 
                .contactName(request.getContactName())
                .contactNumber(request.getContactNumber())
                .email(request.getEmail())
                .phone(request.getPhone())
                .notes(request.getNotes())
                .createdBy(currentUser)
                .build();
        return AddClientResponse.from(clientRepository.save(client));
    }
    
    @Cacheable(value = "clients", key = "#currentUser.id")
    public List<AddClientResponse> getAllClients(User currentUser) {
        return clientRepository.findByCreatedBy(currentUser).stream()
                .map(AddClientResponse::from)
                .collect(Collectors.toList());
    }

    public AddClientResponse getClientById(Long id, User currentUser) {
        return clientRepository.findByIdAndCreatedBy(id, currentUser)
                .map(AddClientResponse::from)
                .orElseThrow(() -> new BusinessException("Client not found with id: " + id));
    }

    @CacheEvict(value = "clients", key = "#currentUser.id")
    public AddClientResponse updateClientById(long id, AddClientRequest request, User currentUser) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
        client.setCompanyName(request.getCompanyName());
        client.setIndustry(request.getIndustry());
        client.setContactName(request.getContactName());
        client.setContactNumber(request.getContactNumber());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setNotes(request.getNotes());

        Client saved = clientRepository.save(client);
        return AddClientResponse.from(saved);
    
    }

    @CacheEvict(value = "clients", key = "#currentUser.id")
    public void deleteClientById(long id, User currentUser) {
      Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));        
    
        clientRepository.delete(client);
    
    }

    public byte[] exportReport(User currentUser) {
        List<Client> clients = clientRepository.findByCreatedBy(currentUser);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Clients Report"));
            document.add(new Paragraph("Generated for: " + currentUser.getDisplayName()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            for (String header : new String[]{"Company", "Contact", "Email", "Phone", "Notes"}) {
                PdfPCell cell = new PdfPCell(new Phrase(header));
                cell.setBackgroundColor(new Color(30, 78, 216));
                cell.setPadding(6);
                table.addCell(cell);
            }
            for (Client c : clients) {
                table.addCell(c.getCompanyName() != null ? c.getCompanyName() : "");
                table.addCell(c.getContactName() != null ? c.getContactName() : "");
                table.addCell(c.getEmail() != null ? c.getEmail() : "");
                table.addCell(c.getPhone() != null ? c.getPhone() : "");
                table.addCell(c.getNotes() != null ? c.getNotes() : "");
            }
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Failed to generate report");
        }
    }
}