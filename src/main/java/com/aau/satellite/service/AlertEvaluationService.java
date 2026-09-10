package com.aau.satellite.service;

import com.aau.satellite.domain.Alert;
import com.aau.satellite.domain.AlertStatus;
import com.aau.satellite.domain.Severity;
import com.aau.satellite.domain.TelemetryReading;
import com.aau.satellite.repository.AlertRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Evaluates telemetry readings and creates, updates, escalates, or automatically resolves satellite
 * alerts.
 */
@Service
public class AlertEvaluationService {

  private final AlertRepository repo;
  private final AlertLifecycleService lifecycle;
  private final Clock clock;

  public AlertEvaluationService(
      AlertRepository repo, AlertLifecycleService lifecycle, Clock clock) {
    this.repo = repo;
    this.lifecycle = lifecycle;
    this.clock = clock;
  }

  /**
   * Evaluates a telemetry reading and determines whether an alert should be created, updated,
   * escalated, or resolved.
   *
   * @param reading the telemetry reading being evaluated
   * @param evaluation the result of evaluating the reading
   * @return the affected alert, if one exists
   */
  public Optional<Alert> evaluate(TelemetryReading reading, ReadingEvaluation evaluation) {
    Severity severity = severityFor(evaluation);

    Optional<Alert> openAlert =
        repo.findFirstBySatelliteIdAndStatusIn(
            reading.getSatelliteId(), List.of(AlertStatus.NEW, AlertStatus.ESCALATED));

    // No threshold breach: automatically resolve an existing alert.
    if (severity == Severity.NONE) {
      return openAlert.map(lifecycle::autoResolve);
    }

    // An alert already exists, so update it.
    if (openAlert.isPresent()) {
      Alert alert = openAlert.get();

      alert.setTriggeringReadingId(reading.getId());
      alert.setSeverity(severity);

      // Escalate a NEW alert when the severity becomes CRITICAL.
      if (severity == Severity.CRITICAL && alert.getStatus() == AlertStatus.NEW) {
        lifecycle.escalate(alert);
      }

      return Optional.of(repo.save(alert));
    }

    // No existing alert: create a new one.
    String type = determineAlertType(evaluation);

    Alert alert =
        new Alert(
            reading.getSatelliteId(),
            severity,
            reading.getId(),
            Instant.now(clock),
            type,
            "Automatic threshold/anomaly detection");

    return Optional.of(repo.save(alert));
  }

  /**
   * Determines the alert severity from the number of breached conditions.
   *
   * @param breachCount number of breached telemetry conditions
   * @return calculated severity
   */
  public Severity severityFor(int breachCount) {
    if (breachCount == 0) {
      return Severity.NONE;
    }

    if (breachCount == 1) {
      return Severity.WARNING;
    }

    return Severity.CRITICAL;
  }

  /**
   * Determines the alert severity for a full reading evaluation.
   *
   * <p>Packet loss above {@code TelemetryThresholds.PACKET_LOSS_CRITICAL} forces CRITICAL
   * immediately, regardless of how many other conditions are breached: a near-total
   * communications loss is a bigger deal on its own than the simple breach count would suggest.
   * Everything else falls back to the plain breach-count rule below.
   *
   * @param evaluation the result of evaluating the reading
   * @return calculated severity
   */
  public Severity severityFor(ReadingEvaluation evaluation) {
    if (evaluation.packetLossCritical()) {
      return Severity.CRITICAL;
    }

    return severityFor(evaluation.breachCount());
  }

  /**
   * Determines the primary alert type based on the telemetry condition that was breached.
   *
   * @param evaluation telemetry evaluation result
   * @return alert type
   */
  private String determineAlertType(ReadingEvaluation evaluation) {

    if (evaluation.tempCritical()) {
      return "TEMPERATURE";
    }

    if (evaluation.voltageLow()) {
      return "BATTERY";
    }

    if (evaluation.signalWeak()) {
      return "SIGNAL";
    }

    return "COMMUNICATION";
  }
}
