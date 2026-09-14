package com.aau.satellite.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "telemetry_readings")
public class TelemetryReading {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false)
  String satelliteId;

  @Column(nullable = false)
  Instant recordedAt;

  @Column(nullable = false)
  Double temperatureC;

  @Column(nullable = false)
  Double batteryVoltage;

  @Column(nullable = false)
  Double batteryCurrent;

  @Column(nullable = false)
  Double batteryPercentage;

  @Column(nullable = false)
  Double signalStrengthDb;

  @Column(nullable = false)
  Double packetLossPercent;

  Double communicationLatencyMs;
  Double latitude;
  Double longitude;
  Double altitudeKm;

  protected TelemetryReading() {}

  public TelemetryReading(
      String s,
      Instant t,
      Double temp,
      Double v,
      Double cur,
      Double pct,
      Double sig,
      Double loss,
      Double latn,
      Double lon,
      Double alt) {
    satelliteId = s;
    recordedAt = t;
    temperatureC = temp;
    batteryVoltage = v;
    batteryCurrent = cur;
    batteryPercentage = pct;
    signalStrengthDb = sig;
    packetLossPercent = loss;
    latitude = latn;
    longitude = lon;
    altitudeKm = alt;
  }

  public Long getId() {
    return id;
  }

  public String getSatelliteId() {
    return satelliteId;
  }

  public Instant getRecordedAt() {
    return recordedAt;
  }

  public Double getTemperatureC() {
    return temperatureC;
  }

  public Double getBatteryVoltage() {
    return batteryVoltage;
  }

  public Double getBatteryCurrent() {
    return batteryCurrent;
  }

  public Double getBatteryPercentage() {
    return batteryPercentage;
  }

  public Double getSignalStrengthDb() {
    return signalStrengthDb;
  }

  public Double getPacketLossPercent() {
    return packetLossPercent;
  }

  public Double getCommunicationLatencyMs() {
    return communicationLatencyMs;
  }

  public void setCommunicationLatencyMs(Double v) {
    communicationLatencyMs = v;
  }

  public Double getLatitude() {
    return latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public Double getAltitudeKm() {
    return altitudeKm;
  }
}
