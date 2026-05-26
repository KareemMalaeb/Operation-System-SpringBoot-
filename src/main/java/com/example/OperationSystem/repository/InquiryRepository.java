package com.example.OperationSystem.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.OperationSystem.dto.response.DashboardStatsResponse;
import com.example.OperationSystem.entity.Inquiry;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.enums.InquiryStatus;
@Repository 
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    @Query("SELECT i.code FROM Inquiry i WHERE i.code LIKE :prefix% ORDER BY i.code DESC")
    List<String> findCodesByPrefix(@Param("prefix") String prefix);

    List<Inquiry> findByCreatedBy(User createdBy);
    List<Inquiry> findByAssignedTo(User assignedTo);

    // ── Dashboard queries ────────────────────────────────────────────────

    // Single query — returns every status and its count in one DB round-trip
    @Query("SELECT i.status, COUNT(i) FROM Inquiry i GROUP BY i.status")
    List<Object[]> countGroupedByStatus();

    // Total inquiries per assigned operator
    @Query("SELECT i.assignedTo.id, i.assignedTo.username, i.assignedTo.role, COUNT(i) " +
           "FROM Inquiry i WHERE i.assignedTo IS NOT NULL " +
           "GROUP BY i.assignedTo.id, i.assignedTo.username, i.assignedTo.role")
    List<Object[]> findTotalByAssignee();

    // Active (not WON/LOST) per assigned operator
    @Query("SELECT i.assignedTo.id, COUNT(i) FROM Inquiry i " +
           "WHERE i.assignedTo IS NOT NULL AND i.status NOT IN :statuses " +
           "GROUP BY i.assignedTo.id")
    List<Object[]> findActiveByAssignee(@Param("statuses") Collection<InquiryStatus> statuses);

    // Won per assigned operator
    @Query("SELECT i.assignedTo.id, COUNT(i) FROM Inquiry i " +
           "WHERE i.assignedTo IS NOT NULL AND i.status = :status " +
           "GROUP BY i.assignedTo.id")
    List<Object[]> findWonByAssignee(@Param("status") InquiryStatus status);
}


