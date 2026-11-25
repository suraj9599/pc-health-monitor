
package com.pcmonitor.controller;

import com.pcmonitor.dto.HealthMonitoringResponse;
import com.pcmonitor.service.HealthMonitoringService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monitoring")
public class HealthMonitoringController {

    private final HealthMonitoringService monitoringService;

    public HealthMonitoringController(
            HealthMonitoringService monitoringService) {

        this.monitoringService = monitoringService;
    }

    @GetMapping("/health")
    public HealthMonitoringResponse getHealthMetrics() {

        return monitoringService.getHealthMetrics();
    }
}
