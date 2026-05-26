package com.example.OperationSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OperationSystem.entity.Inquiry;
import com.example.OperationSystem.entity.StatusHistory;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {
    // Returns history ordered oldest → newest for the timeline
    List<StatusHistory> findByInquiryOrderByChangedAtAsc(Inquiry inquiry);
}
