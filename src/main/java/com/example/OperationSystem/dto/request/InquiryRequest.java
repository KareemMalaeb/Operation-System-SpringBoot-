package com.example.OperationSystem.dto.request;

import com.example.OperationSystem.enums.ContainerType;
import com.example.OperationSystem.enums.Encoterms;
import com.example.OperationSystem.enums.FreightType;
import com.example.OperationSystem.enums.DG;
import lombok.Data;

@Data
public class InquiryRequest {

    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String origin;
    private String destination;
    private Encoterms encoterms;
    private DG dg;
    private String supplierLocation;
    private FreightType freightType;
    private ContainerType containerType;
    private String additionalDetails;
}
