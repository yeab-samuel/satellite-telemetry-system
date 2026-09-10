package com.aau.satellite.repository;

import com.aau.satellite.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
  List<Alert> findAllByOrderByCreatedAtDesc();

  List<Alert> findByStatusOrderByCreatedAtDesc(AlertStatus status);

  Optional<Alert> findFirstBySatelliteIdAndStatusIn(String satelliteId, List<AlertStatus> statuses);
}
