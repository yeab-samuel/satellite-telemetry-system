package com.aau.satellite;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

@Disabled("Run in Docker-enabled integration profile")
class PostgresContainerIT {
  @Test
  void postgres_starts() {
    try (PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:16-alpine")) {
      db.start();
      assertTrue(db.isRunning());
    }
  }
}
