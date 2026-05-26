package com.example.OperationSystem.service;

import com.example.OperationSystem.dto.response.DashboardStatsResponse;
import com.example.OperationSystem.enums.InquiryStatus;
import com.example.OperationSystem.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.OperationSystem.enums.InquiryStatus.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InquiryRepository inquiryRepository;

    public DashboardStatsResponse getStats() {

        // ── Step 1: one query → map of status → count ───────────────────
        Map<InquiryStatus, Long> statusMap = inquiryRepository
                .countGroupedByStatus()
                .stream()
                .collect(Collectors.toMap(
                        row -> (InquiryStatus) row[0],
                        row -> (Long)          row[1]
                ));

        // Derive all KPIs from the map — no extra queries needed
        long total      = statusMap.values().stream().mapToLong(Long::longValue).sum();
        long pending    = get(statusMap, NEW) + get(statusMap, ASSIGNED);
        long inProgress = get(statusMap, SENT_TO_AGENTS)
                        + get(statusMap, RECEIVING_QUOTES)
                        + get(statusMap, QUOTES_COMPLETED);
        long quoted     = get(statusMap, QUOTED_TO_CLIENT);
        long won        = get(statusMap, WON);
        long lost       = get(statusMap, LOST);

        // Build ordered byStatus map
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (InquiryStatus s : InquiryStatus.values()) {
            byStatus.put(s.name(), statusMap.getOrDefault(s, 0L));
        }

        // ── Step 2: three queries → workload per operator ───────────────
        // userId → [displayName, role]
        Map<Long, String[]> nameMap  = new LinkedHashMap<>();
        Map<Long, Long>     totals   = new HashMap<>();
        Map<Long, Long>     actives  = new HashMap<>();
        Map<Long, Long>     wons     = new HashMap<>();

        inquiryRepository.findTotalByAssignee().forEach(row -> {
            Long   id   = (Long)   row[0];
            String name = (String) row[1];
            String role = row[2].toString();         // Role enum → "OPERATOR" etc.
            Long   cnt  = (Long)   row[3];
            nameMap.put(id, new String[]{ name, role });
            totals.put(id, cnt);
        });

        inquiryRepository.findActiveByAssignee(List.of(WON, LOST))
                .forEach(row -> actives.put((Long) row[0], (Long) row[1]));

        inquiryRepository.findWonByAssignee(WON)
                .forEach(row -> wons.put((Long) row[0], (Long) row[1]));

        List<DashboardStatsResponse.WorkloadItem> workload = nameMap.entrySet().stream()
                .map(e -> DashboardStatsResponse.WorkloadItem.builder()
                        .userId(e.getKey())
                        .userName(e.getValue()[0])
                        .role(e.getValue()[1])
                        .total(totals.getOrDefault(e.getKey(), 0L))
                        .active(actives.getOrDefault(e.getKey(), 0L))
                        .won(wons.getOrDefault(e.getKey(), 0L))
                        .build())
                .sorted(Comparator.comparingLong(
                        DashboardStatsResponse.WorkloadItem::getTotal).reversed())
                .toList();

        return DashboardStatsResponse.builder()
                .totalInquiries(total)
                .pending(pending)
                .inProgress(inProgress)
                .quoted(quoted)
                .won(won)
                .lost(lost)
                .byStatus(byStatus)
                .workload(workload)
                .build();
    }

    private long get(Map<InquiryStatus, Long> map, InquiryStatus key) {
        return map.getOrDefault(key, 0L);
    }
}