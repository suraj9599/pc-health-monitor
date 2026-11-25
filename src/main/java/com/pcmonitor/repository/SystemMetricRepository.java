
package com.pcmonitor.repository;

import com.pcmonitor.entity.SystemMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SystemMetricRepository extends JpaRepository<SystemMetric, Long> {

    // Fetch the latest 100 monitoring records
    List<SystemMetric> findTop100ByOrderByRecordedAtDesc();

    // Fetch records between two dates
    List<SystemMetric> findByRecordedAtBetweenOrderByRecordedAtAsc(
            LocalDateTime start,
            LocalDateTime end
    );

    // Delete records older than the specified date
    @Modifying
    @Query("DELETE FROM SystemMetric m WHERE m.recordedAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") LocalDateTime cutoff);
}
