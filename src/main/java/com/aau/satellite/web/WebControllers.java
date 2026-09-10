package com.aau.satellite.web;

import com.aau.satellite.domain.*;
import com.aau.satellite.repository.*;
import com.aau.satellite.service.*;
import java.security.Principal;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebControllers {
  final SatelliteRepository sats;
  final GroundStationRepository stations;
  final TelemetryReadingRepository readings;
  final AlertRepository alerts;
  final MissionRepository missions;
  final SatelliteCommandRepository commands;
  final AuditLogRepository audit;
  final SatelliteLifecycleService satLife;
  final AlertLifecycleService alertLife;
  final MissionService missionService;
  final CommandService commandService;
  final SatelliteConfigurationRepository configs;
  final ConfigurationValidationService configValidation;
  final AuditService auditService;

  public WebControllers(
      SatelliteRepository s,
      GroundStationRepository g,
      TelemetryReadingRepository r,
      AlertRepository a,
      MissionRepository m,
      SatelliteCommandRepository c,
      AuditLogRepository l,
      SatelliteLifecycleService sl,
      AlertLifecycleService al,
      MissionService ms,
      CommandService cs,
      SatelliteConfigurationRepository cf,
      ConfigurationValidationService cv,
      AuditService as) {
    sats = s;
    stations = g;
    readings = r;
    alerts = a;
    missions = m;
    commands = c;
    audit = l;
    satLife = sl;
    alertLife = al;
    missionService = ms;
    commandService = cs;
    configs = cf;
    configValidation = cv;
    auditService = as;
  }

  @GetMapping("/login")
  String login(
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String logout,
      Model model) {
    model.addAttribute("loginError", error != null);
    model.addAttribute("loggedOut", logout != null);
    return "login";
  }

  @GetMapping({"/", "/dashboard"})
  String dash(Model m) {
    m.addAttribute("satellites", sats.findAllByOrderByNameAsc());
    m.addAttribute("alerts", alerts.findAllByOrderByCreatedAtDesc());
    m.addAttribute("missions", missions.findAllByOrderByScheduledStartDesc());
    return "dashboard";
  }

  @GetMapping("/satellites")
  String satellites(
      @RequestParam(required = false) SatelliteStatus status,
      @RequestParam(required = false) String q,
      Model m) {
    List<Satellite> list =
        status == null ? sats.findAllByOrderByNameAsc() : sats.findByStatusOrderByNameAsc(status);
    if (q != null && !q.isBlank())
      list =
          list.stream()
              .filter(
                  s ->
                      s.getCode().toLowerCase().contains(q.toLowerCase())
                          || s.getName().toLowerCase().contains(q.toLowerCase()))
              .toList();
    m.addAttribute("satellites", list);
    m.addAttribute("statuses", SatelliteStatus.values());
    m.addAttribute("selected", status);
    m.addAttribute("q", q);
    return "satellites";
  }

  @GetMapping("/satellites/{id}")
  String detail(@PathVariable Long id, Model m) {
    Satellite s = sats.findById(id).orElseThrow();
    m.addAttribute("satellite", s);
    m.addAttribute("telemetry", readings.findTop50BySatelliteIdOrderByRecordedAtDesc(s.getCode()));
    m.addAttribute("config", configs.findBySatelliteId(id).orElse(null));
    return "satellite-detail";
  }

  @PostMapping("/satellites/{id}/state")
  String state(@PathVariable Long id, @RequestParam SatelliteStatus status, Principal p) {
    satLife.transition(id, status);
    auditService.log(p.getName(), "CHANGE_STATE", "SATELLITE", id.toString(), status.name());
    return "redirect:/satellites/" + id;
  }

  @GetMapping("/alerts")
  String alerts(@RequestParam(required = false) AlertStatus status, Model m) {
    m.addAttribute(
        "alerts",
        status == null
            ? alerts.findAllByOrderByCreatedAtDesc()
            : alerts.findByStatusOrderByCreatedAtDesc(status));
    m.addAttribute("statuses", AlertStatus.values());
    return "alerts";
  }

  @PostMapping("/alerts/{id}/acknowledge")
  String ack(@PathVariable Long id, Principal p) {
    alertLife.acknowledge(id, p.getName());
    auditService.log(p.getName(), "ACKNOWLEDGE", "ALERT", id.toString(), "Alert acknowledged");
    return "redirect:/alerts";
  }

  @PostMapping("/alerts/{id}/resolve")
  String resolve(@PathVariable Long id, Principal p) {
    alertLife.resolve(id, p.getName());
    auditService.log(p.getName(), "RESOLVE", "ALERT", id.toString(), "Alert resolved");
    return "redirect:/alerts";
  }

  @GetMapping("/missions")
  String mission(Model m) {
    m.addAttribute("missions", missions.findAllByOrderByScheduledStartDesc());
    m.addAttribute("satellites", sats.findAllByOrderByNameAsc());
    m.addAttribute("stations", stations.findAllByOrderByNameAsc());
    return "missions";
  }

  @PostMapping("/missions")
  String create(
      @RequestParam String name,
      @RequestParam String description,
      @RequestParam Long satelliteId,
      @RequestParam Long groundStationId,
      @RequestParam String start,
      @RequestParam String end,
      Principal p) {
    missionService.create(
        name,
        description,
        satelliteId,
        groundStationId,
        Instant.parse(start),
        Instant.parse(end),
        p.getName());
    return "redirect:/missions";
  }

  @PostMapping("/missions/{id}/state")
  String missionState(@PathVariable Long id, @RequestParam MissionStatus status, Principal p) {
    missionService.transition(id, status);
    auditService.log(p.getName(), "MISSION_STATE", "MISSION", id.toString(), status.name());
    return "redirect:/missions";
  }

  @GetMapping("/commands")
  String commands(Model m) {
    m.addAttribute("commands", commands.findAllByOrderByCreatedAtDesc());
    m.addAttribute("satellites", sats.findAllByOrderByNameAsc());
    return "commands";
  }

  @PostMapping("/commands")
  String command(
      @RequestParam Long satelliteId,
      @RequestParam CommandType type,
      Principal p,
      org.springframework.security.core.Authentication a) {
    String role = a.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
    commandService.issue(satelliteId, type, role, p.getName());
    auditService.log(p.getName(), "SEND_COMMAND", "COMMAND", satelliteId.toString(), type.name());
    return "redirect:/commands";
  }

  @GetMapping("/configuration/{satelliteId}")
  String cfg(@PathVariable Long satelliteId, Model m) {
    m.addAttribute("satellite", sats.findById(satelliteId).orElseThrow());
    m.addAttribute("config", configs.findBySatelliteId(satelliteId).orElseThrow());
    return "configuration";
  }

  @PostMapping("/configuration/{satelliteId}")
  String updateCfg(
      @PathVariable Long satelliteId,
      @RequestParam double interval,
      @RequestParam double tempMin,
      @RequestParam double tempMax,
      @RequestParam double batteryMin,
      @RequestParam double batteryMax,
      @RequestParam double signalMin,
      @RequestParam double packetLossMax,
      Principal p) {
    configValidation.validate(
        interval, tempMin, tempMax, batteryMin, batteryMax, signalMin, packetLossMax);
    SatelliteConfiguration c = configs.findBySatelliteId(satelliteId).orElseThrow();
    if (c.getSatellite().getStatus() == SatelliteStatus.DECOMMISSIONED)
      throw new IllegalStateException("Decommissioned satellite cannot be configured");
    c.update(interval, tempMin, tempMax, batteryMin, batteryMax, signalMin, packetLossMax);
    configs.save(c);
    auditService.log(
        p.getName(),
        "UPDATE_CONFIGURATION",
        "SATELLITE",
        satelliteId.toString(),
        "Telemetry thresholds changed");
    return "redirect:/satellites/" + satelliteId;
  }

  @GetMapping("/audit")
  String audit(Model m) {
    m.addAttribute("logs", audit.findTop100ByOrderByTimestampDesc());
    return "audit";
  }
}
