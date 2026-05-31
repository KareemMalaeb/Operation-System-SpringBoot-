package com.example.OperationSystem.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OperationSystem.entity.Inquiry;
import com.example.OperationSystem.entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    void deleteByInquiry(Inquiry inquiry);
}
