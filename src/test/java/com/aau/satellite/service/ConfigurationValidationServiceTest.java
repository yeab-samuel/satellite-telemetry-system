package com.aau.satellite.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

class ConfigurationValidationServiceTest {
  ConfigurationValidationService s = new ConfigurationValidationService();

  @Test
  void interval_boundaries() {
    s.validate(5, -20, 70, 6.5, 9, -100, 15);
    s.validate(300, -20, 70, 6.5, 9, -100, 15);
    assertThrows(IllegalArgumentException.class, () -> s.validate(4.99, -20, 70, 6.5, 9, -100, 15));
    assertThrows(
        IllegalArgumentException.class, () -> s.validate(300.01, -20, 70, 6.5, 9, -100, 15));
  }

  @Test
  void cross_field_rules() {
    assertThrows(IllegalArgumentException.class, () -> s.validate(30, 70, -20, 6.5, 9, -100, 15));
    assertThrows(IllegalArgumentException.class, () -> s.validate(30, -20, 70, 9, 6.5, -100, 15));
    assertThrows(IllegalArgumentException.class, () -> s.validate(30, -20, 70, 6.5, 9, -100, 101));
  }

  @Test
  void battery_range_boundaries() {
    s.validate(30, -20, 70, 0, 15, -100, 15);
    assertThrows(IllegalArgumentException.class, () -> s.validate(30, -20, 70, -0.01, 9, -100, 15));
    assertThrows(IllegalArgumentException.class, () -> s.validate(30, -20, 70, 6.5, 15.01, -100, 15));
  }

  @Test
  void signal_range_boundaries() {
    s.validate(30, -20, 70, 6.5, 9, -120, 15);
    s.validate(30, -20, 70, 6.5, 9, -20, 15);
    assertThrows(
            IllegalArgumentException.class, () -> s.validate(30, -20, 70, 6.5, 9, -120.01, 15));
    assertThrows(IllegalArgumentException.class, () -> s.validate(30, -20, 70, 6.5, 9, -19.99, 15));
  }

  @Test
  void packet_loss_lower_bound() {
    s.validate(30, -20, 70, 6.5, 9, -100, 0);
    assertThrows(IllegalArgumentException.class, () -> s.validate(30, -20, 70, 6.5, 9, -100, -0.01));
  }
}
