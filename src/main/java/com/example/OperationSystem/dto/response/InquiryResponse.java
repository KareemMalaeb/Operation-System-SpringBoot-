package com.example.OperationSystem.dto.response;

import com.example.OperationSystem.entity.Inquiry;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InquiryResponse {

    private Long id;
    private String code;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String origin;
    private String destination;
    private String encoterms;
    private String dg;
    private String supplierLocation;
    private String freightType;
    private String containerType;
    private String status;
    private String plFilePath;
    private String ciFilePath;
    private String additionalDetails;
    private String createdBy;
    private String assignedTo;
    private String createdAt;
    private String updatedAt;

    public static InquiryResponse from(Inquiry i) {
        InquiryResponse r = new InquiryResponse();
        r.setId(i.getId());
        r.setCode(i.getCode());
        r.setClientName(i.getClientName());
        r.setClientEmail(i.getClientEmail());
        r.setClientPhone(i.getClientPhone());
        r.setOrigin(i.getOrigin());
        r.setDestination(i.getDestination());
        r.setFreightType(i.getFreightType() != null ? i.getFreightType().getLabel() : null);
        r.setContainerType(i.getContainerType() != null ? i.getContainerType().getLabel() : null);
        r.setEncoterms(i.getEncoterms() != null ? i.getEncoterms().getLabel() : null);
        r.setDg(i.getDg() != null ? i.getDg().getLabel() : null);
        r.setSupplierLocation(i.getSupplierLocation());
        r.setStatus(i.getStatus().name());
        r.setPlFilePath(i.getPlFilePath());
        r.setCiFilePath(i.getCiFilePath());
        r.setAdditionalDetails(i.getAdditionalDetails());
        r.setCreatedBy(i.getCreatedBy() != null ? i.getCreatedBy().getDisplayName() : null);
        r.setAssignedTo(i.getAssignedTo() != null ? i.getAssignedTo().getDisplayName() : null);
        r.setCreatedAt(i.getCreatedAt() != null ? i.getCreatedAt().toString() : null);
        r.setUpdatedAt(i.getUpdatedAt() != null ? i.getUpdatedAt().toString() : null);
        return r;
    }
}
