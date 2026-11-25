
package com.pcmonitor.repository;

import com.pcmonitor.entity.SystemAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SystemAlertRepository
        extends JpaRepository<SystemAlert, Long> {

    Optional<SystemAlert> findTopByMetricTypeOrderByCreatedAtDesc(
            String metricType
    );

    List<SystemAlert> findTop100ByOrderByCreatedAtDesc();


    @Query("""
    SELECT a FROM SystemAlert a
    WHERE (:metricType IS NULL OR a.metricType = :metricType)
      AND (:status IS NULL OR a.status = :status)
    ORDER BY a.createdAt DESC
    """)
    List<SystemAlert> searchAlerts(
            @Param("metricType") String metricType,
            @Param("status") String status,
            Pageable pageable
    );

}
