package com.aau.satellite.service;

import org.springframework.stereotype.Service;

@Service
public class ConfigurationValidationService {
  public void validate(
      double interval,
      double tmin,
      double tmax,
      double bmin,
      double bmax,
      double signal,
      double packet) {
    if (interval < 5 || interval > 300)
      throw new IllegalArgumentException("Telemetry interval must be 5-300 seconds");
    if (tmin >= tmax)
      throw new IllegalArgumentException("Minimum temperature must be below maximum");
    if (bmin >= bmax) throw new IllegalArgumentException("Minimum battery must be below maximum");
    if (bmin < 0 || bmax > 15)
      throw new IllegalArgumentException("Battery limits must be within 0-15V");
    if (signal < -120 || signal > -20)
      throw new IllegalArgumentException("Signal threshold must be -120 to -20 dBm");
    if (packet < 0 || packet > 100)
      throw new IllegalArgumentException("Packet loss must be 0-100 percent");
  }
}
