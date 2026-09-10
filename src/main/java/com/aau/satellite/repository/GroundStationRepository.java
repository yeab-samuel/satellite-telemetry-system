package com.aau.satellite.repository;

import com.aau.satellite.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroundStationRepository extends JpaRepository<GroundStation, Long> {
  Optional<GroundStation> findByCode(String code);

  List<GroundStation> findAllByOrderByNameAsc();
}
