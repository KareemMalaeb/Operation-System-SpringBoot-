package com.example.OperationSystem.dto.response;

import com.example.OperationSystem.entity.Inquiry;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class InquirySummaryResponse {
    
    private Long id;
    private String code;
    private String clientName;
    private String clientEmail;
    private String clientPhone;

    private String origin;
    private String destination;
    private String freightType;
    private String containerType;

    private String status;

    private String CreatedBy;
    private String AssignedTo;

    private String createdAt;
    private String updatedAt;


    public static InquirySummaryResponse from(Inquiry i) {
        return InquirySummaryResponse.builder()
                .id(i.getId())
                .code(i.getCode())
                .clientName(i.getClientName())
                .clientEmail(i.getClientEmail())
                .clientPhone(i.getClientPhone())
                .origin(i.getOrigin())
                .destination(i.getDestination())
                .freightType(i.getFreightType() != null ? i.getFreightType().name() : null)
                .containerType(i.getContainerType() != null ? i.getContainerType().name() : null)
                .status(i.getStatus().name())
                .CreatedBy(i.getCreatedBy() != null ? i.getCreatedBy().getDisplayName() : null)
                .AssignedTo(i.getAssignedTo() != null ? i.getAssignedTo().getDisplayName() : null)
                .createdAt(i.getCreatedAt().toString())
                .updatedAt(i.getUpdatedAt().toString())
                .build();
  



    }
}
