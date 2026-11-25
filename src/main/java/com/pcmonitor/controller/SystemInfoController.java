
package com.pcmonitor.controller;

import com.pcmonitor.dto.SystemInfoResponse;
import com.pcmonitor.service.SystemInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemInfoController {

    private final SystemInfoService systemInfoService;

    public SystemInfoController(
            SystemInfoService systemInfoService) {

        this.systemInfoService = systemInfoService;
    }

    @GetMapping("/info")
    public SystemInfoResponse getSystemInfo() {

        return systemInfoService.getSystemInformation();
    }
}
