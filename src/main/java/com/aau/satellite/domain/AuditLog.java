package com.aau.satellite.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  String username;
  String action;
  String entityType;
  String entityId;
  Instant timestamp;

  @Column(length = 2000)
  String details;

  protected AuditLog() {}

  public AuditLog(String u, String a, String t, String i, String d) {
    username = u;
    action = a;
    entityType = t;
    entityId = i;
    details = d;
    timestamp = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getAction() {
    return action;
  }

  public String getEntityType() {
    return entityType;
  }

  public String getEntityId() {
    return entityId;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public String getDetails() {
    return details;
  }
}
