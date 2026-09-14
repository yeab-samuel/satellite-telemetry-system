package com.aau.satellite.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.AlertRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.mockito.*;

class AlertLifecycleServiceTest {
    @Mock AlertRepository repo;
    AlertLifecycleService s;
    Clock clock = Clock.fixed(Instant.parse("2026-09-10T10:00:00Z"), ZoneOffset.UTC);

    @BeforeEach
    void set() {
        MockitoAnnotations.openMocks(this);
        s = new AlertLifecycleService(repo, clock);
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    private Alert alertWithStatus(AlertStatus status) {
        Alert a = new Alert("SAT-001", Severity.WARNING, 1L, Instant.now(clock), "TYPE", "desc");
        a.setStatus(status);
        when(repo.findById(1L)).thenReturn(Optional.of(a));
        return a;
    }

    @Test
    void acknowledge_succeeds_when_alert_is_new() {
        alertWithStatus(AlertStatus.NEW);
        Alert result = s.acknowledge(1L, "op");
        assertEquals(AlertStatus.ACKNOWLEDGED, result.getStatus());
        assertEquals("op", result.getAcknowledgedBy());
    }

    @Test
    void acknowledge_succeeds_when_alert_is_escalated() {
        alertWithStatus(AlertStatus.ESCALATED);
        Alert result = s.acknowledge(1L, "op");
        assertEquals(AlertStatus.ACKNOWLEDGED, result.getStatus());
    }

    @Test
    void acknowledge_rejects_already_acknowledged_alert() {
        alertWithStatus(AlertStatus.ACKNOWLEDGED);
        assertThrows(InvalidStateTransitionException.class, () -> s.acknowledge(1L, "op"));
    }

    @Test
    void acknowledge_rejects_resolved_alert() {
        alertWithStatus(AlertStatus.RESOLVED);
        assertThrows(InvalidStateTransitionException.class, () -> s.acknowledge(1L, "op"));
    }

    @Test
    void acknowledge_throws_when_alert_not_found() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> s.acknowledge(99L, "op"));
    }

    @Test
    void resolve_succeeds_when_alert_is_acknowledged() {
        alertWithStatus(AlertStatus.ACKNOWLEDGED);
        Alert result = s.resolve(1L, "op");
        assertEquals(AlertStatus.RESOLVED, result.getStatus());
        assertEquals("op", result.getResolvedBy());
    }

    @Test
    void resolve_rejects_new_alert() {
        alertWithStatus(AlertStatus.NEW);
        assertThrows(InvalidStateTransitionException.class, () -> s.resolve(1L, "op"));
    }

    @Test
    void resolve_throws_when_alert_not_found() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> s.resolve(99L, "op"));
    }

    @Test
    void autoResolve_succeeds_for_open_alert() {
        Alert a = alertWithStatus(AlertStatus.NEW);
        Alert result = s.autoResolve(a);
        assertEquals(AlertStatus.RESOLVED, result.getStatus());
    }

    @Test
    void autoResolve_rejects_already_resolved_alert() {
        Alert a = alertWithStatus(AlertStatus.RESOLVED);
        assertThrows(InvalidStateTransitionException.class, () -> s.autoResolve(a));
    }

    @Test
    void escalate_succeeds_for_new_alert() {
        Alert a = alertWithStatus(AlertStatus.NEW);
        Alert result = s.escalate(a);
        assertEquals(AlertStatus.ESCALATED, result.getStatus());
    }

    @Test
    void escalate_rejects_non_new_alert() {
        Alert a = alertWithStatus(AlertStatus.ACKNOWLEDGED);
        assertThrows(InvalidStateTransitionException.class, () -> s.escalate(a));
    }
}