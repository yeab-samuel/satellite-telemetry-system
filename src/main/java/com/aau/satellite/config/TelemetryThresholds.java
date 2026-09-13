package com.aau.satellite.config;

public final class TelemetryThresholds {
  private TelemetryThresholds() {}

  public static final double VOLTAGE_CRITICAL_LOW = 6.5,
      VOLTAGE_LOW = 7.0,
      VOLTAGE_HIGH = 8.4,
      VOLTAGE_CRITICAL_HIGH = 9.0;
  public static final double TEMP_CRITICAL_LOW = -20,
      TEMP_LOW = -10,
      TEMP_HIGH = 60,
      TEMP_CRITICAL_HIGH = 70;
  public static final double SIGNAL_CRITICAL_WEAK = -100, SIGNAL_WEAK = -90, SIGNAL_STRONG = -30;
  public static final double PACKET_LOSS_DEGRADED = 5, PACKET_LOSS_CRITICAL = 15;
}
