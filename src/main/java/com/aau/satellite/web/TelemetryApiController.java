package com.aau.satellite.web;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.*;
import com.aau.satellite.service.*;
import jakarta.validation.Valid;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TelemetryApiController {
  final TelemetryIngestionService ingestion;
  final TelemetryReadingRepository readings;
  final AlertRepository alerts;

  public TelemetryApiController(
      TelemetryIngestionService i, TelemetryReadingRepository r, AlertRepository a) {
    ingestion = i;
    readings = r;
    alerts = a;
  }

  @PostMapping("/telemetry")
  public ResponseEntity<TelemetryReading> submit(@Valid @RequestBody TelemetryReadingRequest q) {
    TelemetryReading r =
        new TelemetryReading(
            q.getSatelliteId(),
            Instant.now(),
            q.getTemperatureC(),
            q.getBatteryVoltage(),
            q.getBatteryCurrent(),
            q.getBatteryPercentage(),
            q.getSignalStrengthDb(),
            q.getPacketLossPercent(),
            q.getLatitude(),
            q.getLongitude(),
            q.getAltitudeKm());
    r.setCommunicationLatencyMs(q.getCommunicationLatencyMs());
    return ResponseEntity.status(201).body(ingestion.ingest(r).reading());
  }

  @GetMapping("/telemetry/{id}/latest")
  public ResponseEntity<TelemetryReading> latest(@PathVariable String id) {
    return readings
        .findFirstBySatelliteIdOrderByRecordedAtDesc(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/alerts")
  public List<Alert> alerts(@RequestParam(required = false) AlertStatus status) {
    return status == null
        ? alerts.findAllByOrderByCreatedAtDesc()
        : alerts.findByStatusOrderByCreatedAtDesc(status);
  }
}
