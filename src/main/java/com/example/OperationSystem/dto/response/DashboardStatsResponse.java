package com.example.OperationSystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardStatsResponse {

    private long totalInquiries;
    private long pending;           // NEW + ASSIGNED
    private long inProgress;        // SENT_TO_AGENTS + RECEIVING_QUOTES + QUOTES_COMPLETED
    private long quoted;            // QUOTED_TO_CLIENT
    private long won;
    private long lost;

    private Map<String, Long> byStatus;     // { "NEW": 2, "WON": 5, ... }
    private List<WorkloadItem> workload;    // per-user inquiry counts

    @Data
    @Builder
    public static class WorkloadItem {
        private Long userId;
        private String userName;
        private String role;
        private long total;
        private long active;
        private long won;
    }
}
