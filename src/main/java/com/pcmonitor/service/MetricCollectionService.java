
package com.pcmonitor.service;

import com.pcmonitor.dto.HealthMonitoringResponse;
import com.pcmonitor.dto.NetworkMonitoringResponse;
import com.pcmonitor.entity.SystemMetric;
import com.pcmonitor.repository.SystemMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricCollectionService {

    private final SystemMetricRepository systemMetricRepository;
    private final HealthMonitoringService healthMonitoringService;
    private final NetworkMonitoringService networkMonitoringService;
    private final AlertMonitoringService alertMonitoringService;

    @Scheduled(fixedDelayString = "${monitoring.collection-interval:30000}")
    public void collectAndSaveMetrics() {

        HealthMonitoringResponse health =
                healthMonitoringService.getHealthMetrics();

        // Check CPU, RAM and disk status and save alerts if needed.
        alertMonitoringService.checkAlerts(health);

        List<NetworkMonitoringResponse> networkData =
                networkMonitoringService.getNetworkInformation();

        long totalBytesReceived = 0;
        long totalBytesSent = 0;

        // Avoid counting the same network adapter more than once.
        Set<String> countedAdapters = new HashSet<>();

        for (NetworkMonitoringResponse network : networkData) {

            if (!network.connected()) {
                continue;
            }

            String adapterKey = network.macAddress();

            if (adapterKey == null || adapterKey.isBlank()) {
                adapterKey = network.interfaceName();
            }

            if (adapterKey != null && countedAdapters.add(adapterKey)) {
                totalBytesReceived += network.bytesReceived();
                totalBytesSent += network.bytesSent();
            }
        }

        SystemMetric metric = new SystemMetric();

        metric.setCpuUsage(health.cpuUsagePercent());
        metric.setRamUsage(health.ramUsagePercent());
        metric.setDiskUsage(health.diskUsagePercent());

        metric.setRamUsedGb(health.ramUsedGB());
        metric.setRamAvailableGb(health.ramAvailableGB());
        metric.setDiskFreeGb(health.diskFreeGB());

        metric.setOverallStatus(health.overallStatus());

        metric.setNetworkBytesReceived(totalBytesReceived);
        metric.setNetworkBytesSent(totalBytesSent);

        metric.setRecordedAt(LocalDateTime.now());

        systemMetricRepository.save(metric);

        log.info("System metrics collected and saved successfully.");
    }
}
