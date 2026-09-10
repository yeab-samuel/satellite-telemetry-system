package com.aau.satellite.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TelemetryApiIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired AlertRepository alerts;

  @Test
  void telemetry_ingestion_creates_alert_for_critical_input() throws Exception {
    String json =
        "{\"satelliteId\":\"SAT-001\",\"temperatureC\":80,\"batteryVoltage\":6.0,\"batteryCurrent\":1.2,\"batteryPercentage\":20,\"signalStrengthDb\":-110,\"packetLossPercent\":20,\"latitude\":9.0,\"longitude\":38.7,\"altitudeKm\":500,\"communicationLatencyMs\":120}";
    mvc.perform(post("/api/telemetry").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.satelliteId").value("SAT-001"));
    Assertions.assertFalse(alerts.findAllByOrderByCreatedAtDesc().isEmpty());
  }

  @Test
  void missing_satellite_id_is_rejected() throws Exception {
    mvc.perform(
            post("/api/telemetry")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"temperatureC\":20}"))
        .andExpect(status().isBadRequest());
  }
}
