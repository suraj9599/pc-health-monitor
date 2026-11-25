
package com.pcmonitor.dto;

public record HealthMonitoringResponse(

        double cpuUsagePercent,

        double ramUsagePercent,

        double diskUsagePercent,

        double ramUsedGB,

        double ramAvailableGB,

        double diskFreeGB,

        String cpuStatus,

        String ramStatus,

        String diskStatus,

        String overallStatus

) {
}
