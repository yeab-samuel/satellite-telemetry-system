package com.aau.satellite.service;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.SatelliteRepository;
import org.springframework.stereotype.Service;

@Service
public class SatelliteLifecycleService {
  final SatelliteRepository repo;

  public SatelliteLifecycleService(SatelliteRepository r) {
    repo = r;
  }

  public Satellite transition(Long id, SatelliteStatus target) {
    Satellite s =
        repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Satellite not found"));
    SatelliteStatus from = s.getStatus();
    boolean ok =
        switch (from) {
          case REGISTERED ->
              target == SatelliteStatus.COMMISSIONING || target == SatelliteStatus.DECOMMISSIONED;
          case COMMISSIONING ->
              target == SatelliteStatus.OPERATIONAL || target == SatelliteStatus.DECOMMISSIONED;
          case OPERATIONAL ->
              target == SatelliteStatus.DEGRADED
                  || target == SatelliteStatus.SAFE_MODE
                  || target == SatelliteStatus.DECOMMISSIONED;
          case DEGRADED ->
              target == SatelliteStatus.SAFE_MODE
                  || target == SatelliteStatus.OPERATIONAL
                  || target == SatelliteStatus.DECOMMISSIONED;
          case SAFE_MODE ->
              target == SatelliteStatus.OPERATIONAL || target == SatelliteStatus.DECOMMISSIONED;
          case DECOMMISSIONED -> false;
        };
    if (!ok) throw new InvalidStateTransitionException(from + " -> " + target + " is not allowed");
    s.setStatus(target);
    return repo.save(s);
  }
}
