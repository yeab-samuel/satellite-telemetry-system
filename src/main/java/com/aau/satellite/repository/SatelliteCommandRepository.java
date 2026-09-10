package com.aau.satellite.repository;

import com.aau.satellite.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SatelliteCommandRepository extends JpaRepository<SatelliteCommand, Long> {
  List<SatelliteCommand> findAllByOrderByCreatedAtDesc();
}
