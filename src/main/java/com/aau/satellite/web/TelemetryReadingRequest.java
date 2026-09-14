package com.aau.satellite.web;

import jakarta.validation.constraints.*;

public class TelemetryReadingRequest {
  @NotBlank String satelliteId;
  @NotNull
  Double temperatureC,
      batteryVoltage,
      batteryCurrent,
      batteryPercentage,
      signalStrengthDb,
      packetLossPercent;
  Double latitude, longitude, altitudeKm, communicationLatencyMs;

  public String getSatelliteId() {
    return satelliteId;
  }

  public void setSatelliteId(String v) {
    satelliteId = v;
  }

  public Double getTemperatureC() {
    return temperatureC;
  }

  public void setTemperatureC(Double v) {
    temperatureC = v;
  }

  public Double getBatteryVoltage() {
    return batteryVoltage;
  }

  public void setBatteryVoltage(Double v) {
    batteryVoltage = v;
  }

  public Double getBatteryCurrent() {
    return batteryCurrent;
  }

  public void setBatteryCurrent(Double v) {
    batteryCurrent = v;
  }

  public Double getBatteryPercentage() {
    return batteryPercentage;
  }

  public void setBatteryPercentage(Double v) {
    batteryPercentage = v;
  }

  public Double getSignalStrengthDb() {
    return signalStrengthDb;
  }

  public void setSignalStrengthDb(Double v) {
    signalStrengthDb = v;
  }

  public Double getPacketLossPercent() {
    return packetLossPercent;
  }

  public void setPacketLossPercent(Double v) {
    packetLossPercent = v;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double v) {
    latitude = v;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double v) {
    longitude = v;
  }

  public Double getAltitudeKm() {
    return altitudeKm;
  }

  public void setAltitudeKm(Double v) {
    altitudeKm = v;
  }

  public Double getCommunicationLatencyMs() {
    return communicationLatencyMs;
  }

  public void setCommunicationLatencyMs(Double v) {
    communicationLatencyMs = v;
  }
}
