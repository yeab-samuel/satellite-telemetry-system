package com.aau.satellite.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.AlertRepository;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

class AlertEvaluationServiceTest {
  @Mock AlertRepository repo;
  @Mock AlertLifecycleService lifecycle;
  AlertEvaluationService s;

  @BeforeEach
  void set() {
    MockitoAnnotations.openMocks(this);
    s =
        new AlertEvaluationService(
            repo, lifecycle, Clock.fixed(Instant.parse("2026-09-01T00:00:00Z"), ZoneOffset.UTC));
  }

  @Test
  void decision_table_zero_one_two() {
    assertEquals(Severity.NONE, s.severityFor(0));
    assertEquals(Severity.WARNING, s.severityFor(1));
    assertEquals(Severity.CRITICAL, s.severityFor(2));
    assertEquals(Severity.CRITICAL, s.severityFor(4));
  }

  @Test
  void creates_warning_for_single_breach() {
    TelemetryReading r =
        new TelemetryReading("SAT", Instant.now(), 20., 7.5, 1., 80., -50., 0., 0., 0., 500.);
    ReadingEvaluation e =
        new ReadingEvaluation(
            Partition.NORMAL, Partition.NORMAL, Partition.NORMAL, false, false, false, true);
    when(repo.findFirstBySatelliteIdAndStatusIn(any(), any())).thenReturn(Optional.empty());
    when(repo.save(any(Alert.class))).thenAnswer(i -> i.getArgument(0));
    var a = s.evaluate(r, e).orElseThrow();
    assertEquals(Severity.WARNING, a.getSeverity());
    verify(repo).save(any(Alert.class));
  }

  @Test
  void severity_none_with_existing_alert_triggers_autoresolve() {
    TelemetryReading r =
            new TelemetryReading("SAT", Instant.now(), 20., 12., 1., 80., -50., 0., 0., 0., 500.);
    ReadingEvaluation e =
            new ReadingEvaluation(
                    Partition.NORMAL, Partition.NORMAL, Partition.NORMAL, false, false, false, false);
    Alert existing = new Alert("SAT", Severity.WARNING, 1L, Instant.now(), "BATTERY", "d");
    when(repo.findFirstBySatelliteIdAndStatusIn(any(), any())).thenReturn(Optional.of(existing));
    when(lifecycle.autoResolve(existing)).thenReturn(existing);

    Optional<Alert> result = s.evaluate(r, e);

    verify(lifecycle).autoResolve(existing);
    assertTrue(result.isPresent());
  }

  @Test
  void severity_none_with_no_existing_alert_returns_empty() {
    TelemetryReading r =
            new TelemetryReading("SAT", Instant.now(), 20., 12., 1., 80., -50., 0., 0., 0., 500.);
    ReadingEvaluation e =
            new ReadingEvaluation(
                    Partition.NORMAL, Partition.NORMAL, Partition.NORMAL, false, false, false, false);
    when(repo.findFirstBySatelliteIdAndStatusIn(any(), any())).thenReturn(Optional.empty());

    Optional<Alert> result = s.evaluate(r, e);

    assertTrue(result.isEmpty());
    verify(lifecycle, never()).autoResolve(any());
  }

  @Test
  void existing_new_alert_escalates_when_severity_becomes_critical() {
    TelemetryReading r =
            new TelemetryReading("SAT", Instant.now(), 20., 3., 1., 80., -50., 0., 0., 0., 500.);
    ReadingEvaluation e =
            new ReadingEvaluation(
                    Partition.CRITICAL_LOW, Partition.NORMAL, Partition.NORMAL, true, true, false, false);
    Alert existing = new Alert("SAT", Severity.WARNING, 1L, Instant.now(), "BATTERY", "d");
    when(repo.findFirstBySatelliteIdAndStatusIn(any(), any())).thenReturn(Optional.of(existing));
    when(repo.save(any(Alert.class))).thenAnswer(i -> i.getArgument(0));

    s.evaluate(r, e);

    verify(lifecycle).escalate(existing);
  }

  @Test
  void existing_already_escalated_alert_is_not_re_escalated() {
    TelemetryReading r =
            new TelemetryReading("SAT", Instant.now(), 20., 3., 1., 80., -50., 0., 0., 0., 500.);
    ReadingEvaluation e =
            new ReadingEvaluation(
                    Partition.CRITICAL_LOW, Partition.NORMAL, Partition.NORMAL, true, true, false, false);
    Alert existing = new Alert("SAT", Severity.CRITICAL, 1L, Instant.now(), "BATTERY", "d");
    existing.setStatus(AlertStatus.ESCALATED);
    when(repo.findFirstBySatelliteIdAndStatusIn(any(), any())).thenReturn(Optional.of(existing));
    when(repo.save(any(Alert.class))).thenAnswer(i -> i.getArgument(0));

    s.evaluate(r, e);

    verify(lifecycle, never()).escalate(any());
  }

  @Test
  void existing_alert_updated_without_escalation_when_still_warning() {
    TelemetryReading r =
            new TelemetryReading("SAT", Instant.now(), 20., 7.5, 1., 80., -50., 0., 0., 0., 500.);
    ReadingEvaluation e =
            new ReadingEvaluation(
                    Partition.LOW, Partition.NORMAL, Partition.NORMAL, true, false, false, false);
    Alert existing = new Alert("SAT", Severity.WARNING, 1L, Instant.now(), "BATTERY", "d");
    when(repo.findFirstBySatelliteIdAndStatusIn(any(), any())).thenReturn(Optional.of(existing));
    when(repo.save(any(Alert.class))).thenAnswer(i -> i.getArgument(0));

    Alert result = s.evaluate(r, e).orElseThrow();

    assertEquals(Severity.WARNING, result.getSeverity());
    verify(lifecycle, never()).escalate(any());
  }

  @Test
  void new_alert_type_is_temperature_when_temp_critical() {
    TelemetryReading r =
            new TelemetryReading("SAT", Instant.now(), 90., 7.5, 1., 80., -50., 0., 0., 0., 500.);
    ReadingEvaluation e =
            new ReadingEvaluation(
                    Partition.NORMAL, Partition.CRITICAL_HIGH, Partition.NORMAL, false, false, true, false);
    when(repo.findFirstBySatelliteIdAndStatusIn(any(), any())).thenReturn(Optional.empty());
    when(repo.save(any(Alert.class))).thenAnswer(i -> i.getArgument(0));

    Alert result = s.evaluate(r, e).orElseThrow();

    assertEquals("TEMPERATURE", result.getAlertType());
  }

  @Test
  void new_alert_type_is_battery_when_voltage_low() {
    TelemetryReading r =
            new TelemetryReading("SAT", Instant.now(), 20., 3., 1., 80., -50., 0., 0., 0., 500.);
    ReadingEvaluation e =
            new ReadingEvaluation(
                    Partition.CRITICAL_LOW, Partition.NORMAL, Partition.NORMAL, true, false, false, false);
    when(repo.findFirstBySatelliteIdAndStatusIn(any(), any())).thenReturn(Optional.empty());
    when(repo.save(any(Alert.class))).thenAnswer(i -> i.getArgument(0));

    Alert result = s.evaluate(r, e).orElseThrow();

    assertEquals("BATTERY", result.getAlertType());
  }

  @Test
  void new_alert_type_is_signal_when_signal_weak() {
    TelemetryReading r =
            new TelemetryReading("SAT", Instant.now(), 20., 7.5, 1., 80., -119., 0., 0., 0., 500.);
    ReadingEvaluation e =
            new ReadingEvaluation(
                    Partition.NORMAL, Partition.NORMAL, Partition.CRITICAL_LOW, false, true, false, false);
    when(repo.findFirstBySatelliteIdAndStatusIn(any(), any())).thenReturn(Optional.empty());
    when(repo.save(any(Alert.class))).thenAnswer(i -> i.getArgument(0));

    Alert result = s.evaluate(r, e).orElseThrow();

    assertEquals("SIGNAL", result.getAlertType());
  }
}
