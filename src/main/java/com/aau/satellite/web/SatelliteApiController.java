package com.aau.satellite.web;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.*;
import com.aau.satellite.service.*;
import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/satellites")
public class SatelliteApiController {
  final SatelliteRepository repo;
  final SatelliteLifecycleService life;

  public SatelliteApiController(SatelliteRepository r, SatelliteLifecycleService l) {
    repo = r;
    life = l;
  }

  @GetMapping
  public List<Satellite> all(@RequestParam(required = false) SatelliteStatus status) {
    return status == null
        ? repo.findAllByOrderByNameAsc()
        : repo.findByStatusOrderByNameAsc(status);
  }

  @GetMapping("/{id}")
  public Satellite one(@PathVariable Long id) {
    return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Satellite not found"));
  }

  @PostMapping("/{id}/state")
  public Satellite state(@PathVariable Long id, @RequestParam SatelliteStatus status) {
    return life.transition(id, status);
  }
}
