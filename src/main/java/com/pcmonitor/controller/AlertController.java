
package com.pcmonitor.controller;

import com.pcmonitor.entity.SystemAlert;
import com.pcmonitor.repository.SystemAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final SystemAlertRepository systemAlertRepository;

    @GetMapping
    public List<SystemAlert> getLatestAlerts() {
        return systemAlertRepository.findTop100ByOrderByCreatedAtDesc();
    }

    @GetMapping("/all")
    public List<SystemAlert> getAllAlerts() {
        return systemAlertRepository.findAll();
    }

    @GetMapping("/filter")
    public List<SystemAlert> filterAlerts(
            @RequestParam(required = false) String metricType,
            @RequestParam(required = false) String status
    ) {
        if (metricType != null) {
            metricType = metricType.trim().toUpperCase(Locale.ROOT);
        }

        if (status != null) {
            status = status.trim().toUpperCase(Locale.ROOT);
        }

        return systemAlertRepository.searchAlerts(
                metricType,
                status,
                PageRequest.of(0, 100)
        );
    }
}
