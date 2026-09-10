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
}
