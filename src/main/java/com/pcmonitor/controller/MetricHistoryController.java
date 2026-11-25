
package com.pcmonitor.controller;

import com.pcmonitor.entity.SystemMetric;
import com.pcmonitor.repository.SystemMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricHistoryController {

    private final SystemMetricRepository systemMetricRepository;

    // Get the latest 100 monitoring records
    @GetMapping("/latest")
    public List<SystemMetric> getLatestMetrics() {
        return systemMetricRepository.findTop100ByOrderByRecordedAtDesc();
    }

    // Get monitoring records between two dates
    @GetMapping("/history")
    public List<SystemMetric> getMetricHistory(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must be before or equal to 'to'.");
        }

        return systemMetricRepository
                .findByRecordedAtBetweenOrderByRecordedAtAsc(from, to);
    }
}
