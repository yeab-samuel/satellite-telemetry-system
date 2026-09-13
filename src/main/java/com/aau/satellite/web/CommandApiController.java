package com.aau.satellite.web;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.*;
import com.aau.satellite.service.*;
import java.util.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commands")
public class CommandApiController {
  final CommandService service;
  final SatelliteCommandRepository repo;

  public CommandApiController(CommandService s, SatelliteCommandRepository r) {
    service = s;
    repo = r;
  }

  @GetMapping
  public List<SatelliteCommand> all() {
    return repo.findAllByOrderByCreatedAtDesc();
  }

  @PostMapping
  public SatelliteCommand issue(
      @RequestParam Long satelliteId, @RequestParam CommandType type, Authentication auth) {
    String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
    return service.issue(satelliteId, type, role, auth.getName());
  }
}
