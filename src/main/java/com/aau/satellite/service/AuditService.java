package com.aau.satellite.service;

import com.aau.satellite.domain.AuditLog;
import com.aau.satellite.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
  final AuditLogRepository repo;

  public AuditService(AuditLogRepository r) {
    repo = r;
  }

  public void log(String u, String a, String t, String id, String d) {
    repo.save(new AuditLog(u, a, t, id, d));
  }
}
