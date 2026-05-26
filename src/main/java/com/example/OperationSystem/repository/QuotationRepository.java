package com.example.OperationSystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.OperationSystem.entity.Inquiry;
import com.example.OperationSystem.entity.Quotation;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    Optional<Quotation> findByInquiryAndIsSelectedTrue(Inquiry inquiry);

    List<Quotation> findByInquiry(Inquiry inquiry);

    @Modifying
    @Query("DELETE FROM Quotation q WHERE q.inquiry = :inquiry")
    void deleteByInquiry(@Param("inquiry") Inquiry inquiry);
}