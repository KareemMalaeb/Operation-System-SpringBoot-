package com.example.OperationSystem.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

import com.example.OperationSystem.entity.StatusHistory;
import com.example.OperationSystem.enums.InquiryStatus;

@Data
@Builder
public class StatusHistoryResponse {

    private InquiryStatus fromStatus;
    private InquiryStatus toStatus;
    private String changedBy;
    private String note;
    private LocalDateTime changedAt;

    public static StatusHistoryResponse from(StatusHistory h) {
        return StatusHistoryResponse.builder()
                .fromStatus(h.getFromStatus())
                .toStatus(h.getToStatus())
                .changedBy(h.getChangedBy() != null ? h.getChangedBy().getDisplayName() : "System")
                .note(h.getNote())
                .changedAt(h.getChangedAt())
                .build();
    }
}