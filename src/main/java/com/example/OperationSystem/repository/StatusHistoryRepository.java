package com.example.OperationSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.OperationSystem.entity.Inquiry;
import com.example.OperationSystem.entity.StatusHistory;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {
    List<StatusHistory> findByInquiryOrderByChangedAtAsc(Inquiry inquiry);

    @Modifying
    @Query("DELETE FROM StatusHistory sh WHERE sh.inquiry = :inquiry")
    void deleteByInquiry(@Param("inquiry") Inquiry inquiry);
}