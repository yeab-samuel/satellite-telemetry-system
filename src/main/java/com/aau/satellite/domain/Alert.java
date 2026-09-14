package com.aau.satellite.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "alerts")
public class Alert {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  String satelliteId;

  @Enumerated(EnumType.STRING)
  AlertStatus status;

  @Enumerated(EnumType.STRING)
  Severity severity;

  Long triggeringReadingId;
  String alertType;
  String description;
  Instant createdAt;
  Instant acknowledgedAt;
  Instant resolvedAt;
  String acknowledgedBy;
  String resolvedBy;

  protected Alert() {}

  public Alert(String sid, Severity sev, Long rid, Instant at, String type, String desc) {
    satelliteId = sid;
    severity = sev;
    triggeringReadingId = rid;
    createdAt = at;
    status = AlertStatus.NEW;
    alertType = type;
    description = desc;
  }

  public Long getId() {
    return id;
  }

  public String getSatelliteId() {
    return satelliteId;
  }

  public AlertStatus getStatus() {
    return status;
  }

  public void setStatus(AlertStatus s) {
    status = s;
  }

  public Severity getSeverity() {
    return severity;
  }

  public void setSeverity(Severity s) {
    severity = s;
  }

  public Long getTriggeringReadingId() {
    return triggeringReadingId;
  }

  public void setTriggeringReadingId(Long v) {
    triggeringReadingId = v;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getAcknowledgedAt() {
    return acknowledgedAt;
  }

  public void setAcknowledgedAt(Instant v) {
    acknowledgedAt = v;
  }

  public Instant getResolvedAt() {
    return resolvedAt;
  }

  public void setResolvedAt(Instant v) {
    resolvedAt = v;
  }

  public String getAcknowledgedBy() {
    return acknowledgedBy;
  }

  public void setAcknowledgedBy(String v) {
    acknowledgedBy = v;
  }

  public String getResolvedBy() {
    return resolvedBy;
  }

  public void setResolvedBy(String v) {
    resolvedBy = v;
  }

  public String getAlertType() {
    return alertType;
  }

  public String getDescription() {
    return description;
  }

  public boolean isOpen() {
    return status == AlertStatus.NEW || status == AlertStatus.ESCALATED;
  }
}
