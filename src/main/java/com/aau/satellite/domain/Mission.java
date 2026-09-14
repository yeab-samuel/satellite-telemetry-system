package com.aau.satellite.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "missions")
public class Mission {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false)
  String name;

  String description;

  @ManyToOne(optional = false)
  Satellite satellite;

  @ManyToOne(optional = false)
  GroundStation groundStation;

  Instant scheduledStart;
  Instant scheduledEnd;

  @Enumerated(EnumType.STRING)
  MissionStatus status;

  String createdBy;

  protected Mission() {}

  public Mission(
      String n, String d, Satellite s, GroundStation g, Instant st, Instant en, String by) {
    name = n;
    description = d;
    satellite = s;
    groundStation = g;
    scheduledStart = st;
    scheduledEnd = en;
    status = MissionStatus.DRAFT;
    createdBy = by;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Satellite getSatellite() {
    return satellite;
  }

  public GroundStation getGroundStation() {
    return groundStation;
  }

  public Instant getScheduledStart() {
    return scheduledStart;
  }

  public Instant getScheduledEnd() {
    return scheduledEnd;
  }

  public MissionStatus getStatus() {
    return status;
  }

  public void setStatus(MissionStatus s) {
    status = s;
  }

  public String getCreatedBy() {
    return createdBy;
  }
}
