package com.aau.satellite.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "satellite_commands")
public class SatelliteCommand {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne(optional = false)
  Satellite satellite;

  @Enumerated(EnumType.STRING)
  CommandType type;

  @Enumerated(EnumType.STRING)
  CommandStatus status;

  String issuedBy;
  Instant createdAt;
  Instant completedAt;
  String resultMessage;

  protected SatelliteCommand() {}

  public SatelliteCommand(Satellite s, CommandType t, String by) {
    satellite = s;
    type = t;
    status = CommandStatus.PENDING;
    issuedBy = by;
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public Satellite getSatellite() {
    return satellite;
  }

  public CommandType getType() {
    return type;
  }

  public CommandStatus getStatus() {
    return status;
  }

  public void setStatus(CommandStatus s) {
    status = s;
  }

  public String getIssuedBy() {
    return issuedBy;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getCompletedAt() {
    return completedAt;
  }

  public void setCompletedAt(Instant i) {
    completedAt = i;
  }

  public String getResultMessage() {
    return resultMessage;
  }

  public void setResultMessage(String s) {
    resultMessage = s;
  }
}
