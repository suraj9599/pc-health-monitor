
package com.pcmonitor.service;

import com.pcmonitor.repository.SystemMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricCleanupService {

    private final SystemMetricRepository systemMetricRepository;

    @Value("${monitoring.retention-days:30}")
    private int retentionDays;

    @Scheduled(cron = "${monitoring.cleanup-cron:0 0 2 * * *}")
    @Transactional
    public void deleteOldMetrics() {

        LocalDateTime cutoffDate =
                LocalDateTime.now().minusDays(retentionDays);

        int deletedRecords =
                systemMetricRepository.deleteOlderThan(cutoffDate);

        log.info(
                "Metric cleanup completed. Deleted {} records older than {} days.",
                deletedRecords,
                retentionDays
        );
    }
}
