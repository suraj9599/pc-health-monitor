
package com.pcmonitor.dto;

public record SystemInfoResponse(
        String computerName,
        String manufacturer,
        String model,
        String operatingSystem,
        String osVersion,
        String architecture,
        String processorName,
        int physicalCores,
        int logicalCores,
        long totalMemoryBytes,
        long availableMemoryBytes,
        long totalDiskBytes,
        long freeDiskBytes,
        long uptimeSeconds,
        String javaVersion
) {
}
