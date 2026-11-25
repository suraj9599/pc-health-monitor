
package com.pcmonitor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_metrics")
@Getter
@Setter
@NoArgsConstructor
public class SystemMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double cpuUsage;

    private double ramUsage;

    private double diskUsage;

    private double ramUsedGb;

    private double ramAvailableGb;

    private double diskFreeGb;

    private String overallStatus;

    private long networkBytesReceived;

    private long networkBytesSent;

    private LocalDateTime recordedAt;

    @PrePersist
    public void onCreate() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }
}
