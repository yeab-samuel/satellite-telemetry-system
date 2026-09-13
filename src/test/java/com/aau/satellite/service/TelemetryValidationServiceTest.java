package com.aau.satellite.service;

import static org.junit.jupiter.api.Assertions.*;

import com.aau.satellite.domain.*;
import java.time.*;
import org.junit.jupiter.api.*;

class TelemetryValidationServiceTest {
  TelemetryValidationService s = new TelemetryValidationService();

  TelemetryReading r(double t, double v, double sig, double loss) {
    return new TelemetryReading("SAT-001", Instant.now(), t, v, 1., 80., sig, loss, 0., 0., 500.);
  }

  @Test
  void voltage_boundaries() {
    assertEquals(Partition.CRITICAL_LOW, s.evaluate(r(20, 6.49, -50, 0)).voltagePartition());
    assertEquals(Partition.LOW, s.evaluate(r(20, 6.5, -50, 0)).voltagePartition());
    assertEquals(Partition.NORMAL, s.evaluate(r(20, 7.0, -50, 0)).voltagePartition());
    assertEquals(Partition.NORMAL, s.evaluate(r(20, 8.4, -50, 0)).voltagePartition());
    assertEquals(Partition.HIGH, s.evaluate(r(20, 8.41, -50, 0)).voltagePartition());
    assertEquals(Partition.CRITICAL_HIGH, s.evaluate(r(20, 9.01, -50, 0)).voltagePartition());
  }

  @Test
  void temperature_partitions() {
    assertEquals(Partition.CRITICAL_LOW, s.evaluate(r(-21, 8, -50, 0)).temperaturePartition());
    assertEquals(Partition.LOW, s.evaluate(r(-10.1, 8, -50, 0)).temperaturePartition());
    assertEquals(Partition.NORMAL, s.evaluate(r(20, 8, -50, 0)).temperaturePartition());
    assertEquals(Partition.HIGH, s.evaluate(r(60.1, 8, -50, 0)).temperaturePartition());
    assertEquals(Partition.CRITICAL_HIGH, s.evaluate(r(71, 8, -50, 0)).temperaturePartition());
  }

  @Test
  void signal_and_packet_loss() {
    var e = s.evaluate(r(20, 8, -101, 6));
    assertTrue(e.signalWeak());
    assertTrue(e.packetLossHigh());
    assertEquals(2, e.breachCount());
  }
}
