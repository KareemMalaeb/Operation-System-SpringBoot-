package com.example.OperationSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OperationSystem.entity.Inquiry;
import com.example.OperationSystem.entity.InquiryAgent;

import java.util.List;

public interface InquiryAgentRepository extends JpaRepository<InquiryAgent, Long> {

    List<InquiryAgent> findByInquiry(Inquiry inquiry);

    boolean existsByInquiryIdAndAgentId(Long inquiryId, Long agentId);
}
