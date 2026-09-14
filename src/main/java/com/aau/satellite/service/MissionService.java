package com.aau.satellite.service;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.*;
import java.time.*;
import org.springframework.stereotype.Service;

@Service
public class MissionService {
  final MissionRepository missions;
  final SatelliteRepository sats;
  final GroundStationRepository stations;

  public MissionService(MissionRepository m, SatelliteRepository s, GroundStationRepository g) {
    missions = m;
    sats = s;
    stations = g;
  }

  public Mission create(
      String name, String desc, Long sid, Long gid, Instant start, Instant end, String user) {
    if (name == null || name.isBlank())
      throw new IllegalArgumentException("Mission name is required");
    if (start == null || end == null || !end.isAfter(start))
      throw new IllegalArgumentException("Mission end must be after start");
    long minutes = Duration.between(start, end).toMinutes();
    if (minutes < 10)
      throw new IllegalArgumentException("Mission duration must be at least 10 minutes");
    if (minutes > 43200)
      throw new IllegalArgumentException("Mission duration cannot exceed 30 days");
    Satellite s =
        sats.findById(sid).orElseThrow(() -> new IllegalArgumentException("Satellite not found"));
    GroundStation g =
        stations
            .findById(gid)
            .orElseThrow(() -> new IllegalArgumentException("Ground station not found"));
    if (g.getStatus() != GroundStationStatus.ONLINE)
      throw new IllegalArgumentException("Ground station must be online");
    if (s.getStatus() == SatelliteStatus.DECOMMISSIONED)
      throw new IllegalArgumentException("Satellite is decommissioned");
    return missions.save(new Mission(name, desc, s, g, start, end, user));
  }

  public Mission transition(Long id, MissionStatus target) {
    Mission m =
        missions.findById(id).orElseThrow(() -> new IllegalArgumentException("Mission not found"));
    boolean ok =
        switch (m.getStatus()) {
          case DRAFT -> target == MissionStatus.SCHEDULED || target == MissionStatus.CANCELLED;
          case SCHEDULED -> target == MissionStatus.ACTIVE || target == MissionStatus.CANCELLED;
          case ACTIVE -> target == MissionStatus.COMPLETED || target == MissionStatus.CANCELLED;
          case COMPLETED, CANCELLED -> false;
        };
    if (!ok)
      throw new InvalidStateTransitionException(
          m.getStatus() + " -> " + target + " is not allowed");
    m.setStatus(target);
    return missions.save(m);
  }
}
