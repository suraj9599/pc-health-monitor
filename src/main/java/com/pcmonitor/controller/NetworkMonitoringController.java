
package com.pcmonitor.controller;

import com.pcmonitor.dto.NetworkMonitoringResponse;
import com.pcmonitor.service.NetworkMonitoringService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
public class NetworkMonitoringController {

    private final NetworkMonitoringService networkService;

    public NetworkMonitoringController(
            NetworkMonitoringService networkService) {

        this.networkService = networkService;
    }

    @GetMapping("/network")
    public List<NetworkMonitoringResponse> getNetworkInformation() {

        return networkService.getNetworkInformation();
    }
}
