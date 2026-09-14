package com.aau.satellite.service;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.TelemetryReadingRepository;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TelemetryIngestionService {
  final TelemetryReadingRepository repo;
  final TelemetryValidationService validation;
  final AlertEvaluationService alerts;

  public TelemetryIngestionService(
      TelemetryReadingRepository r, TelemetryValidationService v, AlertEvaluationService a) {
    repo = r;
    validation = v;
    alerts = a;
  }

  @Transactional
  public IngestionResult ingest(TelemetryReading r) {
    TelemetryReading saved = repo.save(r);
    ReadingEvaluation e = validation.evaluate(saved);
    return new IngestionResult(saved, e, alerts.evaluate(saved, e).orElse(null));
  }

  public record IngestionResult(
      TelemetryReading reading, ReadingEvaluation evaluation, Alert alert) {}
}
