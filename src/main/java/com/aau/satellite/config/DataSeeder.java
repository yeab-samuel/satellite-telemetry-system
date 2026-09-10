package com.aau.satellite.config;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.*;
import java.time.Instant;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

  private final UserRepository users;
  private final GroundStationRepository stations;
  private final SatelliteRepository satellites;
  private final SatelliteConfigurationRepository configurations;
  private final PasswordEncoder passwordEncoder;

  public DataSeeder(
      UserRepository users,
      GroundStationRepository stations,
      SatelliteRepository satellites,
      SatelliteConfigurationRepository configurations,
      PasswordEncoder passwordEncoder) {
    this.users = users;
    this.stations = stations;
    this.satellites = satellites;
    this.configurations = configurations;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {
    seedUser("admin", "admin123", Role.ADMIN);
    seedUser("operator", "operator123", Role.OPERATOR);
    seedUser("engineer", "engineer123", Role.ENGINEER);
    seedUser("viewer", "viewer123", Role.VIEWER);

    GroundStation station =
        stations
            .findByCode("GS-ADD-01")
            .orElseGet(
                () ->
                    stations.save(
                        new GroundStation(
                            "GS-ADD-01",
                            "Addis Ground Station",
                            "Addis Ababa",
                            GroundStationStatus.ONLINE,
                            4,
                            "S-Band")));

    seedSatellite(
        "SAT-001",
        "Abyssinia-1",
        "CubeSat-X",
        "LEO",
        "2025-06-01T00:00:00Z",
        SatelliteStatus.OPERATIONAL,
        station,
        30,
        -20,
        70,
        6.5,
        9,
        -100,
        15);

    seedSatellite(
        "SAT-002",
        "Abyssinia-2",
        "CubeSat-Y",
        "LEO",
        "2026-01-10T00:00:00Z",
        SatelliteStatus.COMMISSIONING,
        station,
        60,
        -15,
        65,
        6.8,
        8.8,
        -95,
        10);
  }

  private void seedUser(String username, String password, Role role) {
    User user = users.findByUsername(username).orElseGet(() -> new User(username, "", role));

    user.setPasswordHash(passwordEncoder.encode(password));
    user.setRole(role);
    users.save(user);
  }

  private void seedSatellite(
      String code,
      String name,
      String model,
      String orbit,
      String launchDate,
      SatelliteStatus status,
      GroundStation station,
      double interval,
      double tempMin,
      double tempMax,
      double batteryMin,
      double batteryMax,
      double signalMin,
      double packetLossMax) {
    Satellite satellite =
        satellites
            .findByCode(code)
            .orElseGet(
                () ->
                    satellites.save(
                        new Satellite(code, name, model, orbit, Instant.parse(launchDate))));

    satellite.setStatus(status);
    satellite.setGroundStation(station);
    satellites.save(satellite);

    configurations
        .findBySatelliteId(satellite.getId())
        .ifPresentOrElse(
            ignored -> {},
            () ->
                configurations.save(
                    new SatelliteConfiguration(
                        satellite,
                        interval,
                        tempMin,
                        tempMax,
                        batteryMin,
                        batteryMax,
                        signalMin,
                        packetLossMax)));
  }
}
