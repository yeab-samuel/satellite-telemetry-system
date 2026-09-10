package com.aau.satellite.repository;

import com.aau.satellite.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelemetryReadingRepository extends JpaRepository<TelemetryReading, Long> {
  Optional<TelemetryReading> findFirstBySatelliteIdOrderByRecordedAtDesc(String satelliteId);

  List<TelemetryReading> findTop50BySatelliteIdOrderByRecordedAtDesc(String satelliteId);
}
