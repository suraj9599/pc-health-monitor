
package com.pcmonitor.service;

import com.pcmonitor.dto.HealthMonitoringResponse;
import com.pcmonitor.entity.SystemAlert;
import com.pcmonitor.repository.SystemAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlertMonitoringService {

    private final SystemAlertRepository systemAlertRepository;

    public void checkAlerts(HealthMonitoringResponse health) {

        checkMetric(
                "CPU",
                health.cpuStatus(),
                health.cpuUsagePercent(),
                75.0,
                90.0
        );

        checkMetric(
                "RAM",
                health.ramStatus(),
                health.ramUsagePercent(),
                75.0,
                90.0
        );

        checkMetric(
                "DISK",
                health.diskStatus(),
                health.diskUsagePercent(),
                80.0,
                90.0
        );
    }

    private void checkMetric(
            String metricType,
            String currentStatus,
            double metricValue,
            double warningThreshold,
            double criticalThreshold
    ) {

        String status = currentStatus.toUpperCase(Locale.ROOT);

        Optional<SystemAlert> latestAlert =
                systemAlertRepository
                        .findTopByMetricTypeOrderByCreatedAtDesc(metricType);

        String previousStatus = latestAlert
                .map(alert -> alert.getStatus())
                .orElse("NORMAL");

        // Do not create duplicate alerts when the status has not changed.
        if (status.equals(previousStatus)) {
            return;
        }

        // Record recovery when a previously unhealthy component becomes normal.
        if (status.equals("NORMAL")) {

            if (!previousStatus.equals("NORMAL")
                    && !previousStatus.equals("RECOVERED")) {

                saveAlert(
                        metricType,
                        "RECOVERED",
                        metricValue,
                        null,
                        metricType + " usage has returned to normal."
                );
            }

            return;
        }

        if (!status.equals("WARNING") && !status.equals("CRITICAL")) {
            return;
        }

        double threshold = status.equals("CRITICAL")
                ? criticalThreshold
                : warningThreshold;

        String message = status.equals("CRITICAL")
                ? metricType + " usage is critically high."
                : metricType + " usage is above the warning threshold.";

        saveAlert(
                metricType,
                status,
                metricValue,
                threshold,
                message
        );
    }

    private void saveAlert(
            String metricType,
            String status,
            double metricValue,
            Double thresholdValue,
            String message
    ) {

        SystemAlert alert = new SystemAlert();

        alert.setMetricType(metricType);
        alert.setStatus(status);
        alert.setMetricValue(metricValue);
        alert.setThresholdValue(thresholdValue);
        alert.setMessage(message);

        systemAlertRepository.save(alert);
    }
}
