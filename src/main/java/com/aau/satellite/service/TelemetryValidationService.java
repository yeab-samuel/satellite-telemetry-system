package com.aau.satellite.service;

import com.aau.satellite.config.TelemetryThresholds;
import com.aau.satellite.domain.*;
import org.springframework.stereotype.Service;

@Service
public class TelemetryValidationService {
  public ReadingEvaluation evaluate(TelemetryReading r) {
    Partition v = classifyVoltage(r.getBatteryVoltage()),
        t = classifyTemperature(r.getTemperatureC()),
        s = classifySignal(r.getSignalStrengthDb());
    return new ReadingEvaluation(
        v,
        t,
        s,
        v == Partition.CRITICAL_LOW || v == Partition.LOW,
        s == Partition.CRITICAL_LOW || s == Partition.LOW,
        t == Partition.CRITICAL_LOW || t == Partition.CRITICAL_HIGH,
        r.getPacketLossPercent() > TelemetryThresholds.PACKET_LOSS_DEGRADED);
  }

  Partition classifyVoltage(double x) {
    if (x < TelemetryThresholds.VOLTAGE_CRITICAL_LOW) return Partition.CRITICAL_LOW;
    if (x < TelemetryThresholds.VOLTAGE_LOW) return Partition.LOW;
    if (x <= TelemetryThresholds.VOLTAGE_HIGH) return Partition.NORMAL;
    if (x <= TelemetryThresholds.VOLTAGE_CRITICAL_HIGH) return Partition.HIGH;
    return Partition.CRITICAL_HIGH;
  }

  Partition classifyTemperature(double x) {
    if (x < TelemetryThresholds.TEMP_CRITICAL_LOW) return Partition.CRITICAL_LOW;
    if (x < TelemetryThresholds.TEMP_LOW) return Partition.LOW;
    if (x <= TelemetryThresholds.TEMP_HIGH) return Partition.NORMAL;
    if (x <= TelemetryThresholds.TEMP_CRITICAL_HIGH) return Partition.HIGH;
    return Partition.CRITICAL_HIGH;
  }

  Partition classifySignal(double x) {
    if (x < TelemetryThresholds.SIGNAL_CRITICAL_WEAK) return Partition.CRITICAL_LOW;
    if (x < TelemetryThresholds.SIGNAL_WEAK) return Partition.LOW;
    if (x <= TelemetryThresholds.SIGNAL_STRONG) return Partition.NORMAL;
    return Partition.HIGH;
  }
}
