package com.aau.satellite.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.SatelliteRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.mockito.*;

class SatelliteLifecycleServiceTest {
    @Mock SatelliteRepository repo;
    SatelliteLifecycleService s;

    @BeforeEach
    void set() {
        MockitoAnnotations.openMocks(this);
        s = new SatelliteLifecycleService(repo);
    }

    private Satellite satelliteWithStatus(SatelliteStatus status) {
        Satellite sat = new Satellite("S", "S", "M", "LEO", Instant.now());
        sat.setStatus(status);
        when(repo.findById(1L)).thenReturn(Optional.of(sat));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        return sat;
    }

    @Test
    void registered_can_transition_to_commissioning() {
        satelliteWithStatus(SatelliteStatus.REGISTERED);
        Satellite result = s.transition(1L, SatelliteStatus.COMMISSIONING);
        assertEquals(SatelliteStatus.COMMISSIONING, result.getStatus());
    }

    @Test
    void registered_can_transition_to_decommissioned() {
        satelliteWithStatus(SatelliteStatus.REGISTERED);
        Satellite result = s.transition(1L, SatelliteStatus.DECOMMISSIONED);
        assertEquals(SatelliteStatus.DECOMMISSIONED, result.getStatus());
    }

    @Test
    void registered_cannot_transition_to_operational() {
        satelliteWithStatus(SatelliteStatus.REGISTERED);
        assertThrows(
                InvalidStateTransitionException.class, () -> s.transition(1L, SatelliteStatus.OPERATIONAL));
    }

    @Test
    void commissioning_can_transition_to_operational() {
        satelliteWithStatus(SatelliteStatus.COMMISSIONING);
        Satellite result = s.transition(1L, SatelliteStatus.OPERATIONAL);
        assertEquals(SatelliteStatus.OPERATIONAL, result.getStatus());
    }

    @Test
    void commissioning_can_transition_to_decommissioned() {
        satelliteWithStatus(SatelliteStatus.COMMISSIONING);
        Satellite result = s.transition(1L, SatelliteStatus.DECOMMISSIONED);
        assertEquals(SatelliteStatus.DECOMMISSIONED, result.getStatus());
    }

    @Test
    void commissioning_cannot_transition_to_degraded() {
        satelliteWithStatus(SatelliteStatus.COMMISSIONING);
        assertThrows(
                InvalidStateTransitionException.class, () -> s.transition(1L, SatelliteStatus.DEGRADED));
    }

    @Test
    void operational_can_transition_to_degraded() {
        satelliteWithStatus(SatelliteStatus.OPERATIONAL);
        Satellite result = s.transition(1L, SatelliteStatus.DEGRADED);
        assertEquals(SatelliteStatus.DEGRADED, result.getStatus());
    }

    @Test
    void operational_can_transition_to_safe_mode() {
        satelliteWithStatus(SatelliteStatus.OPERATIONAL);
        Satellite result = s.transition(1L, SatelliteStatus.SAFE_MODE);
        assertEquals(SatelliteStatus.SAFE_MODE, result.getStatus());
    }

    @Test
    void operational_can_transition_to_decommissioned() {
        satelliteWithStatus(SatelliteStatus.OPERATIONAL);
        Satellite result = s.transition(1L, SatelliteStatus.DECOMMISSIONED);
        assertEquals(SatelliteStatus.DECOMMISSIONED, result.getStatus());
    }

    @Test
    void operational_cannot_transition_to_registered() {
        satelliteWithStatus(SatelliteStatus.OPERATIONAL);
        assertThrows(
                InvalidStateTransitionException.class, () -> s.transition(1L, SatelliteStatus.REGISTERED));
    }

    @Test
    void degraded_can_transition_to_safe_mode() {
        satelliteWithStatus(SatelliteStatus.DEGRADED);
        Satellite result = s.transition(1L, SatelliteStatus.SAFE_MODE);
        assertEquals(SatelliteStatus.SAFE_MODE, result.getStatus());
    }

    @Test
    void degraded_can_transition_to_operational() {
        satelliteWithStatus(SatelliteStatus.DEGRADED);
        Satellite result = s.transition(1L, SatelliteStatus.OPERATIONAL);
        assertEquals(SatelliteStatus.OPERATIONAL, result.getStatus());
    }

    @Test
    void degraded_can_transition_to_decommissioned() {
        satelliteWithStatus(SatelliteStatus.DEGRADED);
        Satellite result = s.transition(1L, SatelliteStatus.DECOMMISSIONED);
        assertEquals(SatelliteStatus.DECOMMISSIONED, result.getStatus());
    }

    @Test
    void degraded_cannot_transition_to_registered() {
        satelliteWithStatus(SatelliteStatus.DEGRADED);
        assertThrows(
                InvalidStateTransitionException.class, () -> s.transition(1L, SatelliteStatus.REGISTERED));
    }

    @Test
    void safe_mode_can_transition_to_operational() {
        satelliteWithStatus(SatelliteStatus.SAFE_MODE);
        Satellite result = s.transition(1L, SatelliteStatus.OPERATIONAL);
        assertEquals(SatelliteStatus.OPERATIONAL, result.getStatus());
    }

    @Test
    void safe_mode_can_transition_to_decommissioned() {
        satelliteWithStatus(SatelliteStatus.SAFE_MODE);
        Satellite result = s.transition(1L, SatelliteStatus.DECOMMISSIONED);
        assertEquals(SatelliteStatus.DECOMMISSIONED, result.getStatus());
    }

    @Test
    void safe_mode_cannot_transition_to_degraded() {
        satelliteWithStatus(SatelliteStatus.SAFE_MODE);
        assertThrows(
                InvalidStateTransitionException.class, () -> s.transition(1L, SatelliteStatus.DEGRADED));
    }

    @Test
    void decommissioned_cannot_transition_anywhere() {
        satelliteWithStatus(SatelliteStatus.DECOMMISSIONED);
        assertThrows(
                InvalidStateTransitionException.class, () -> s.transition(1L, SatelliteStatus.OPERATIONAL));
    }

    @Test
    void transition_throws_when_satellite_not_found() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(
                IllegalArgumentException.class, () -> s.transition(99L, SatelliteStatus.OPERATIONAL));
    }
}