package com.aau.satellite.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "satellites", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class Satellite {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false)
  String code;

  @Column(nullable = false)
  String name;

  String model;
  String orbitType;
  Instant launchDate;

  @Enumerated(EnumType.STRING)
  SatelliteStatus status;

  @ManyToOne GroundStation groundStation;
  Instant createdAt;
  Instant updatedAt;

  protected Satellite() {}

  public Satellite(String c, String n, String m, String o, Instant l) {
    code = c;
    name = n;
    model = m;
    orbitType = o;
    launchDate = l;
    status = SatelliteStatus.REGISTERED;
    createdAt = Instant.now();
    updatedAt = createdAt;
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

  public String getModel() {
    return model;
  }

  public String getOrbitType() {
    return orbitType;
  }

  public Instant getLaunchDate() {
    return launchDate;
  }

  public SatelliteStatus getStatus() {
    return status;
  }

  public GroundStation getGroundStation() {
    return groundStation;
  }

  public void setGroundStation(GroundStation g) {
    groundStation = g;
    touch();
  }

  public void setStatus(SatelliteStatus s) {
    status = s;
    touch();
  }

  private void touch() {
    updatedAt = Instant.now();
  }
}
