package com.aau.satellite.domain;

import jakarta.persistence.*;

@Entity
public class SatelliteConfiguration {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @OneToOne(optional = false)
  Satellite satellite;

  double telemetryIntervalSeconds;
  double temperatureMin;
  double temperatureMax;
  double batteryMin;
  double batteryMax;
  double signalMin;
  double packetLossMax;

  protected SatelliteConfiguration() {}

  public SatelliteConfiguration(
      Satellite s,
      double ti,
      double tmin,
      double tmax,
      double bmin,
      double bmax,
      double smin,
      double pl) {
    satellite = s;
    telemetryIntervalSeconds = ti;
    temperatureMin = tmin;
    temperatureMax = tmax;
    batteryMin = bmin;
    batteryMax = bmax;
    signalMin = smin;
    packetLossMax = pl;
  }

  public Long getId() {
    return id;
  }

  public Satellite getSatellite() {
    return satellite;
  }

  public double getTelemetryIntervalSeconds() {
    return telemetryIntervalSeconds;
  }

  public double getTemperatureMin() {
    return temperatureMin;
  }

  public double getTemperatureMax() {
    return temperatureMax;
  }

  public double getBatteryMin() {
    return batteryMin;
  }

  public double getBatteryMax() {
    return batteryMax;
  }

  public double getSignalMin() {
    return signalMin;
  }

  public double getPacketLossMax() {
    return packetLossMax;
  }

  public void update(
      double ti, double tmin, double tmax, double bmin, double bmax, double smin, double pl) {
    telemetryIntervalSeconds = ti;
    temperatureMin = tmin;
    temperatureMax = tmax;
    batteryMin = bmin;
    batteryMax = bmax;
    signalMin = smin;
    packetLossMax = pl;
  }
}
