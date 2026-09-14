package com.aau.satellite.web;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.*;
import com.aau.satellite.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.security.Principal;
import java.time.Instant;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/missions")
public class MissionApiController {
  final MissionService service;
  final MissionRepository repo;

  public MissionApiController(MissionService s, MissionRepository r) {
    service = s;
    repo = r;
  }

  @GetMapping
  public List<Mission> all() {
    return repo.findAllByOrderByScheduledStartDesc();
  }

  @PostMapping
  public ResponseEntity<Mission> create(@Valid @RequestBody MissionRequest q, Principal p) {
    return ResponseEntity.status(201)
        .body(
            service.create(
                q.name(),
                q.description(),
                q.satelliteId(),
                q.groundStationId(),
                q.start(),
                q.end(),
                p.getName()));
  }

  @PostMapping("/{id}/state")
  public Mission transition(@PathVariable Long id, @RequestParam MissionStatus status) {
    return service.transition(id, status);
  }

  public record MissionRequest(
      @NotBlank String name,
      String description,
      @NotNull Long satelliteId,
      @NotNull Long groundStationId,
      @NotNull Instant start,
      @NotNull Instant end) {}
}
