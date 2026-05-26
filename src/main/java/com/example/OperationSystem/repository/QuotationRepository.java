package com.example.OperationSystem.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OperationSystem.entity.Inquiry;
import com.example.OperationSystem.entity.Quotation;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    Optional<Quotation> findByInquiryAndIsSelectedTrue(Inquiry inquiry);
    
}
