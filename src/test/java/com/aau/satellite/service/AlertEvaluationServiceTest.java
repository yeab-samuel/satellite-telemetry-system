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
            Partition.NORMAL, Partition.NORMAL, Partition.NORMAL, false, false, false, true, false);
    when(repo.findFirstBySatelliteIdAndStatusIn(any(), any())).thenReturn(Optional.empty());
    when(repo.save(any(Alert.class))).thenAnswer(i -> i.getArgument(0));
    var a = s.evaluate(r, e).orElseThrow();
    assertEquals(Severity.WARNING, a.getSeverity());
    verify(repo).save(any(Alert.class));
  }

  @Test
  void packetLossAboveCritical_forcesCritical_evenWithOnlyOneBreach() {
    // Only packet loss is breached (one condition -> would normally be WARNING per the plain
    // breach-count rule), but it's above PACKET_LOSS_CRITICAL, so the override in
    // AlertEvaluationService.severityFor(ReadingEvaluation) should force CRITICAL instead.
    TelemetryReading r =
        new TelemetryReading("SAT", Instant.now(), 20., 7.5, 1., 80., -50., 20., 0., 0., 500.);
    ReadingEvaluation e =
        new ReadingEvaluation(
            Partition.NORMAL, Partition.NORMAL, Partition.NORMAL, false, false, false, true, true);
    when(repo.findFirstBySatelliteIdAndStatusIn(any(), any())).thenReturn(Optional.empty());
    when(repo.save(any(Alert.class))).thenAnswer(i -> i.getArgument(0));

    var a = s.evaluate(r, e).orElseThrow();

    assertEquals(Severity.CRITICAL, a.getSeverity());
  }
}
