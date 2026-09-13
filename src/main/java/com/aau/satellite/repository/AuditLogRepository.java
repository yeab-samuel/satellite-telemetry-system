package com.aau.satellite.repository;

import com.aau.satellite.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
  List<AuditLog> findTop100ByOrderByTimestampDesc();
}
