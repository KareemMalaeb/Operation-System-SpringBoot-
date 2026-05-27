package com.example.OperationSystem.dto.response;

import com.example.OperationSystem.entity.Client;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor      
public class AddClientResponse {
    private Long id;
    private String companyName;
    private String industry;
    private String contactName;
    private String contactNumber;
    private String email;
    private String phone;
    private String notes;
    private String createdBy;
    private String createdAt;
    private String updatedAt;

    public static AddClientResponse from(Client client){
        AddClientResponse r = new AddClientResponse();
        r.setId(client.getId());
        r.setCompanyName(client.getCompanyName());
        r.setIndustry(client.getIndustry());
        r.setContactName(client.getContactName());
        r.setContactNumber(client.getContactNumber());
        r.setEmail(client.getEmail());
        r.setPhone(client.getPhone());
        r.setNotes(client.getNotes());
        r.setCreatedBy(client.getCreatedBy() != null ? client.getCreatedBy().getDisplayName() : null);
        r.setCreatedAt(client.getCreatedAt() != null ? client.getCreatedAt().toString() : null);
        r.setUpdatedAt(client.getUpdatedAt() != null ? client.getUpdatedAt().toString() : null);
        return r;
    }

}
