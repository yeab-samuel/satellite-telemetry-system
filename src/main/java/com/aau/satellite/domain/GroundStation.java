package com.aau.satellite.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "ground_stations", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class GroundStation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false)
  String code;

  @Column(nullable = false)
  String name;

  String location;

  @Enumerated(EnumType.STRING)
  GroundStationStatus status;

  int maxSatellites;
  String frequencyBand;

  protected GroundStation() {}

  public GroundStation(String c, String n, String l, GroundStationStatus s, int m, String f) {
    code = c;
    name = n;
    location = l;
    status = s;
    maxSatellites = m;
    frequencyBand = f;
  }

  public Long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getLocation() {
    return location;
  }

  public GroundStationStatus getStatus() {
    return status;
  }

  public int getMaxSatellites() {
    return maxSatellites;
  }

  public String getFrequencyBand() {
    return frequencyBand;
  }

  public void setStatus(GroundStationStatus s) {
    status = s;
  }
}
