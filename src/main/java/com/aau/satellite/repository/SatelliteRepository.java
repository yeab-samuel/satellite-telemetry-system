package com.aau.satellite.repository;

import com.aau.satellite.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SatelliteRepository extends JpaRepository<Satellite, Long> {
  Optional<Satellite> findByCode(String code);

  List<Satellite> findAllByOrderByNameAsc();

  List<Satellite> findByStatusOrderByNameAsc(SatelliteStatus status);
}
