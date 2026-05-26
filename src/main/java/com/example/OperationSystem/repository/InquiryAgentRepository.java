package com.example.OperationSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.OperationSystem.entity.Inquiry;
import com.example.OperationSystem.entity.InquiryAgent;

public interface InquiryAgentRepository extends JpaRepository<InquiryAgent, Long> {

    List<InquiryAgent> findByInquiry(Inquiry inquiry);

    boolean existsByInquiryIdAndAgentId(Long inquiryId, Long agentId);

    @Modifying
    @Query("DELETE FROM InquiryAgent ia WHERE ia.inquiry = :inquiry")
    void deleteByInquiry(@Param("inquiry") Inquiry inquiry);

    @Modifying
    @Query("DELETE FROM InquiryAgent ia WHERE ia.agent.id = :agentId")
    void deleteByAgentId(@Param("agentId") Long agentId);
}