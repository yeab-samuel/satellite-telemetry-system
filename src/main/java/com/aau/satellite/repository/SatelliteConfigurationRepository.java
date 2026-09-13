package com.aau.satellite.repository;

import com.aau.satellite.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SatelliteConfigurationRepository
    extends JpaRepository<SatelliteConfiguration, Long> {
  Optional<SatelliteConfiguration> findBySatelliteId(Long satelliteId);
}
