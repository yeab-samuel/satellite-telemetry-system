package com.aau.satellite.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.*;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.mockito.*;

class CommandServiceTest {
    @Mock SatelliteCommandRepository repo;
    @Mock SatelliteRepository sats;
    CommandService s;

    @BeforeEach
    void set() {
        MockitoAnnotations.openMocks(this);
        s = new CommandService(repo, sats);
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    private Satellite operationalSatellite() {
        Satellite sat = new Satellite("S", "S", "M", "LEO", Instant.now());
        sat.setStatus(SatelliteStatus.OPERATIONAL);
        return sat;
    }

    @Test
    void issue_throws_when_satellite_not_found() {
        when(sats.findById(1L)).thenReturn(Optional.empty());
        assertThrows(
                IllegalArgumentException.class,
                () -> s.issue(1L, CommandType.REQUEST_TELEMETRY, "OPERATOR", "u"));
    }

    @Test
    void engineer_can_update_configuration() {
        when(sats.findById(1L)).thenReturn(Optional.of(operationalSatellite()));
        SatelliteCommand result = s.issue(1L, CommandType.UPDATE_CONFIGURATION, "ENGINEER", "u");
        assertEquals(CommandStatus.COMPLETED, result.getStatus());
    }

    @Test
    void admin_can_update_configuration() {
        when(sats.findById(1L)).thenReturn(Optional.of(operationalSatellite()));
        SatelliteCommand result = s.issue(1L, CommandType.UPDATE_CONFIGURATION, "ADMIN", "u");
        assertEquals(CommandStatus.COMPLETED, result.getStatus());
    }

    @Test
    void operator_cannot_update_configuration() {
        when(sats.findById(1L)).thenReturn(Optional.of(operationalSatellite()));
        assertThrows(
                SecurityException.class,
                () -> s.issue(1L, CommandType.UPDATE_CONFIGURATION, "OPERATOR", "u"));
    }

    @Test
    void operator_can_request_telemetry() {
        when(sats.findById(1L)).thenReturn(Optional.of(operationalSatellite()));
        SatelliteCommand result = s.issue(1L, CommandType.REQUEST_TELEMETRY, "OPERATOR", "u");
        assertEquals(CommandStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getCompletedAt());
        assertNotNull(result.getResultMessage());
    }

    @Test
    void engineer_can_request_telemetry() {
        when(sats.findById(1L)).thenReturn(Optional.of(operationalSatellite()));
        SatelliteCommand result = s.issue(1L, CommandType.REQUEST_TELEMETRY, "ENGINEER", "u");
        assertEquals(CommandStatus.COMPLETED, result.getStatus());
    }

    @Test
    void viewer_cannot_request_telemetry() {
        when(sats.findById(1L)).thenReturn(Optional.of(operationalSatellite()));
        assertThrows(
                SecurityException.class, () -> s.issue(1L, CommandType.REQUEST_TELEMETRY, "VIEWER", "u"));
    }

    @Test
    void issue_rejects_decommissioned_satellite() {
        Satellite sat = new Satellite("S", "S", "M", "LEO", Instant.now());
        sat.setStatus(SatelliteStatus.DECOMMISSIONED);
        when(sats.findById(1L)).thenReturn(Optional.of(sat));
        assertThrows(
                IllegalArgumentException.class,
                () -> s.issue(1L, CommandType.REQUEST_TELEMETRY, "OPERATOR", "u"));
    }
}