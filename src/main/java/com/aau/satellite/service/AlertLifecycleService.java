package com.aau.satellite.service;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.AlertRepository;
import java.time.*;
import org.springframework.stereotype.Service;

@Service
public class AlertLifecycleService {
  final AlertRepository repo;
  final Clock clock;

  public AlertLifecycleService(AlertRepository r, Clock c) {
    repo = r;
    clock = c;
  }

  public Alert acknowledge(Long id, String user) {
    Alert a = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Alert not found"));
    if (!a.isOpen())
      throw new InvalidStateTransitionException("Cannot acknowledge " + a.getStatus());
    a.setStatus(AlertStatus.ACKNOWLEDGED);
    a.setAcknowledgedAt(Instant.now(clock));
    a.setAcknowledgedBy(user);
    return repo.save(a);
  }

  public Alert resolve(Long id, String user) {
    Alert a = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Alert not found"));
    if (a.getStatus() != AlertStatus.ACKNOWLEDGED)
      throw new InvalidStateTransitionException("Cannot resolve " + a.getStatus());
    a.setStatus(AlertStatus.RESOLVED);
    a.setResolvedAt(Instant.now(clock));
    a.setResolvedBy(user);
    return repo.save(a);
  }

  public Alert autoResolve(Alert a) {
    if (!a.isOpen())
      throw new InvalidStateTransitionException("Cannot auto-resolve " + a.getStatus());
    a.setStatus(AlertStatus.RESOLVED);
    a.setResolvedAt(Instant.now(clock));
    return repo.save(a);
  }

  public Alert escalate(Alert a) {
    if (a.getStatus() != AlertStatus.NEW)
      throw new InvalidStateTransitionException("Cannot escalate " + a.getStatus());
    a.setStatus(AlertStatus.ESCALATED);
    return repo.save(a);
  }
}
