package com.aau.satellite.service;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.*;
import java.time.*;
import org.springframework.stereotype.Service;

@Service
public class CommandService {
  final SatelliteCommandRepository repo;
  final SatelliteRepository sats;

  public CommandService(SatelliteCommandRepository r, SatelliteRepository s) {
    repo = r;
    sats = s;
  }

  public SatelliteCommand issue(Long sid, CommandType type, String role, String user) {
    Satellite s =
        sats.findById(sid).orElseThrow(() -> new IllegalArgumentException("Satellite not found"));
    boolean engineer = role.equals("ENGINEER") || role.equals("ADMIN");
    boolean operational = s.getStatus() != SatelliteStatus.DECOMMISSIONED;
    boolean allowed =
        switch (type) {
          case UPDATE_CONFIGURATION -> engineer;
          case REQUEST_TELEMETRY,
                  RESTART_COMMUNICATION,
                  ACTIVATE_SENSOR,
                  DEACTIVATE_SENSOR,
                  ENTER_SAFE_MODE ->
              role.equals("OPERATOR") || engineer;
        };
    if (!allowed) throw new SecurityException("Role cannot issue this command");
    if (!operational) throw new IllegalArgumentException("Satellite is decommissioned");
    SatelliteCommand c = repo.save(new SatelliteCommand(s, type, user));
    c.setStatus(CommandStatus.SENT);
    c.setStatus(CommandStatus.ACKNOWLEDGED);
    c.setStatus(CommandStatus.COMPLETED);
    c.setCompletedAt(Instant.now());
    c.setResultMessage("Command completed by mission simulator");
    return repo.save(c);
  }
}
