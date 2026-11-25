
package com.pcmonitor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "system_alerts",
        indexes = {
                @Index(name = "idx_alert_metric_created",
                        columnList = "metric_type, created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SystemAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_type", nullable = false)
    private String metricType;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, length = 500)
    private String message;

    private Double metricValue;

    private Double thresholdValue;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
