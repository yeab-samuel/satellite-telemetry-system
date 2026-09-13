package com.aau.satellite.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

class MissionServiceTest {
  @Mock MissionRepository missions;
  @Mock SatelliteRepository sats;
  @Mock GroundStationRepository stations;
  MissionService s;

  @BeforeEach
  void set() {
    MockitoAnnotations.openMocks(this);
    s = new MissionService(missions, sats, stations);
  }

  @Test
  void rejects_duration_below_ten_minutes() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            s.create(
                "M",
                "",
                1L,
                2L,
                Instant.parse("2026-09-10T10:00:00Z"),
                Instant.parse("2026-09-10T10:09:00Z"),
                "op"));
  }

  @Test
  void accepts_ten_minutes() {
    Satellite sat = new Satellite("S", "S", "M", "LEO", Instant.now());
    GroundStation g = new GroundStation("G", "G", "A", GroundStationStatus.ONLINE, 2, "S");
    when(sats.findById(1L)).thenReturn(Optional.of(sat));
    when(stations.findById(2L)).thenReturn(Optional.of(g));
    when(missions.save(any())).thenAnswer(i -> i.getArgument(0));
    var m =
        s.create(
            "M",
            "",
            1L,
            2L,
            Instant.parse("2026-09-10T10:00:00Z"),
            Instant.parse("2026-09-10T10:10:00Z"),
            "op");
    assertEquals(MissionStatus.DRAFT, m.getStatus());
  }

  private Mission missionWithStatus(MissionStatus status) {
    Satellite sat = new Satellite("S", "S", "M", "LEO", Instant.now());
    GroundStation g = new GroundStation("G", "G", "A", GroundStationStatus.ONLINE, 2, "S");
    Mission m =
            new Mission(
                    "M",
                    "",
                    sat,
                    g,
                    Instant.parse("2026-09-10T10:00:00Z"),
                    Instant.parse("2026-09-10T10:10:00Z"),
                    "op");
    m.setStatus(status);
    when(missions.findById(1L)).thenReturn(Optional.of(m));
    when(missions.save(any())).thenAnswer(i -> i.getArgument(0));
    return m;
  }

  @Test
  void draft_can_transition_to_scheduled() {
    missionWithStatus(MissionStatus.DRAFT);
    Mission result = s.transition(1L, MissionStatus.SCHEDULED);
    assertEquals(MissionStatus.SCHEDULED, result.getStatus());
  }

  @Test
  void draft_can_transition_to_cancelled() {
    missionWithStatus(MissionStatus.DRAFT);
    Mission result = s.transition(1L, MissionStatus.CANCELLED);
    assertEquals(MissionStatus.CANCELLED, result.getStatus());
  }

  @Test
  void draft_cannot_transition_to_active() {
    missionWithStatus(MissionStatus.DRAFT);
    assertThrows(InvalidStateTransitionException.class, () -> s.transition(1L, MissionStatus.ACTIVE));
  }

  @Test
  void scheduled_can_transition_to_active() {
    missionWithStatus(MissionStatus.SCHEDULED);
    Mission result = s.transition(1L, MissionStatus.ACTIVE);
    assertEquals(MissionStatus.ACTIVE, result.getStatus());
  }

  @Test
  void scheduled_can_transition_to_cancelled() {
    missionWithStatus(MissionStatus.SCHEDULED);
    Mission result = s.transition(1L, MissionStatus.CANCELLED);
    assertEquals(MissionStatus.CANCELLED, result.getStatus());
  }

  @Test
  void scheduled_cannot_transition_to_draft() {
    missionWithStatus(MissionStatus.SCHEDULED);
    assertThrows(InvalidStateTransitionException.class, () -> s.transition(1L, MissionStatus.DRAFT));
  }

  @Test
  void active_can_transition_to_completed() {
    missionWithStatus(MissionStatus.ACTIVE);
    Mission result = s.transition(1L, MissionStatus.COMPLETED);
    assertEquals(MissionStatus.COMPLETED, result.getStatus());
  }

  @Test
  void active_can_transition_to_cancelled() {
    missionWithStatus(MissionStatus.ACTIVE);
    Mission result = s.transition(1L, MissionStatus.CANCELLED);
    assertEquals(MissionStatus.CANCELLED, result.getStatus());
  }

  @Test
  void active_cannot_transition_to_draft() {
    missionWithStatus(MissionStatus.ACTIVE);
    assertThrows(InvalidStateTransitionException.class, () -> s.transition(1L, MissionStatus.DRAFT));
  }

  @Test
  void completed_cannot_transition_anywhere() {
    missionWithStatus(MissionStatus.COMPLETED);
    assertThrows(InvalidStateTransitionException.class, () -> s.transition(1L, MissionStatus.ACTIVE));
  }

  @Test
  void cancelled_cannot_transition_anywhere() {
    missionWithStatus(MissionStatus.CANCELLED);
    assertThrows(InvalidStateTransitionException.class, () -> s.transition(1L, MissionStatus.SCHEDULED));
  }

  @Test
  void transition_throws_when_mission_not_found() {
    when(missions.findById(99L)).thenReturn(Optional.empty());
    assertThrows(IllegalArgumentException.class, () -> s.transition(99L, MissionStatus.SCHEDULED));
  }

  @Test
  void create_rejects_blank_name() {
    assertThrows(
            IllegalArgumentException.class,
            () ->
                    s.create(
                            "   ",
                            "",
                            1L,
                            2L,
                            Instant.parse("2026-09-10T10:00:00Z"),
                            Instant.parse("2026-09-10T10:10:00Z"),
                            "op"));
  }

  @Test
  void create_rejects_end_before_start() {
    assertThrows(
            IllegalArgumentException.class,
            () ->
                    s.create(
                            "M",
                            "",
                            1L,
                            2L,
                            Instant.parse("2026-09-10T10:10:00Z"),
                            Instant.parse("2026-09-10T10:00:00Z"),
                            "op"));
  }

  @Test
  void create_rejects_duration_over_30_days() {
    assertThrows(
            IllegalArgumentException.class,
            () ->
                    s.create(
                            "M",
                            "",
                            1L,
                            2L,
                            Instant.parse("2026-09-10T10:00:00Z"),
                            Instant.parse("2026-10-11T10:00:00Z"),
                            "op"));
  }

  @Test
  void create_rejects_satellite_not_found() {
    when(sats.findById(1L)).thenReturn(Optional.empty());
    assertThrows(
            IllegalArgumentException.class,
            () ->
                    s.create(
                            "M",
                            "",
                            1L,
                            2L,
                            Instant.parse("2026-09-10T10:00:00Z"),
                            Instant.parse("2026-09-10T10:10:00Z"),
                            "op"));
  }

  @Test
  void create_rejects_ground_station_not_online() {
    Satellite sat = new Satellite("S", "S", "M", "LEO", Instant.now());
    GroundStation g = new GroundStation("G", "G", "A", GroundStationStatus.OFFLINE, 2, "S");
    when(sats.findById(1L)).thenReturn(Optional.of(sat));
    when(stations.findById(2L)).thenReturn(Optional.of(g));
    assertThrows(
            IllegalArgumentException.class,
            () ->
                    s.create(
                            "M",
                            "",
                            1L,
                            2L,
                            Instant.parse("2026-09-10T10:00:00Z"),
                            Instant.parse("2026-09-10T10:10:00Z"),
                            "op"));
  }

  @Test
  void create_rejects_decommissioned_satellite() {
    Satellite sat = new Satellite("S", "S", "M", "LEO", Instant.now());
    sat.setStatus(SatelliteStatus.DECOMMISSIONED);
    GroundStation g = new GroundStation("G", "G", "A", GroundStationStatus.ONLINE, 2, "S");
    when(sats.findById(1L)).thenReturn(Optional.of(sat));
    when(stations.findById(2L)).thenReturn(Optional.of(g));
    assertThrows(
            IllegalArgumentException.class,
            () ->
                    s.create(
                            "M",
                            "",
                            1L,
                            2L,
                            Instant.parse("2026-09-10T10:00:00Z"),
                            Instant.parse("2026-09-10T10:10:00Z"),
                            "op"));
  }
}
