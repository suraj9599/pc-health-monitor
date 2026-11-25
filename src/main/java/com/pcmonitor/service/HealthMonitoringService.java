
package com.pcmonitor.service;

import com.pcmonitor.dto.HealthMonitoringResponse;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import org.springframework.stereotype.Service;

@Service
public class HealthMonitoringService {

    private final SystemInfo systemInfo;

    public HealthMonitoringService() {
        this.systemInfo = new SystemInfo();
    }

    public HealthMonitoringResponse getHealthMetrics() {

        var hardware = systemInfo.getHardware();

        // 1. CPU Monitoring
        CentralProcessor processor =
                hardware.getProcessor();

        // Measures CPU utilization over a 1-second interval
        double cpuUsage =
                processor.getSystemCpuLoad(1000) * 100.0;

        // 2. RAM Monitoring
        GlobalMemory memory =
                hardware.getMemory();

        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();

        long usedMemory = totalMemory - availableMemory;

        double ramUsage =
                totalMemory > 0
                        ? (usedMemory * 100.0) / totalMemory
                        : 0;

        // 3. Disk Monitoring
        OperatingSystem os =
                systemInfo.getOperatingSystem();

        FileSystem fileSystem =
                os.getFileSystem();

        long totalDisk = 0;
        long freeDisk = 0;

        for (OSFileStore store : fileSystem.getFileStores()) {

            totalDisk += store.getTotalSpace();
            freeDisk += store.getUsableSpace();
        }

        long usedDisk = totalDisk - freeDisk;

        double diskUsage =
                totalDisk > 0
                        ? (usedDisk * 100.0) / totalDisk
                        : 0;

        // 4. Calculate health statuses
        String cpuStatus = getStatus(cpuUsage, 75, 90);

        String ramStatus = getStatus(ramUsage, 75, 90);

        String diskStatus = getStatus(diskUsage, 80, 90);

        String overallStatus = getOverallStatus(
                cpuStatus,
                ramStatus,
                diskStatus
        );

        // 5. Convert bytes to GB (GiB)
        double ramUsedGB =
                bytesToGB(usedMemory);

        double ramAvailableGB =
                bytesToGB(availableMemory);

        double diskFreeGB =
                bytesToGB(freeDisk);

        return new HealthMonitoringResponse(
                round(cpuUsage),
                round(ramUsage),
                round(diskUsage),
                round(ramUsedGB),
                round(ramAvailableGB),
                round(diskFreeGB),
                cpuStatus,
                ramStatus,
                diskStatus,
                overallStatus
        );
    }

    private String getStatus(
            double usage,
            double warningThreshold,
            double criticalThreshold) {

        if (usage >= criticalThreshold) {
            return "CRITICAL";
        }

        if (usage >= warningThreshold) {
            return "WARNING";
        }

        return "NORMAL";
    }

    private String getOverallStatus(String... statuses) {

        for (String status : statuses) {
            if (status.equals("CRITICAL")) {
                return "CRITICAL";
            }
        }

        for (String status : statuses) {
            if (status.equals("WARNING")) {
                return "WARNING";
            }
        }

        return "NORMAL";
    }

    private double bytesToGB(long bytes) {
        return bytes / (1024.0 * 1024.0 * 1024.0);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
