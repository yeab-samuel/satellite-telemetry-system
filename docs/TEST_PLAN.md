# Test Plan

## Scope
Satellite Mission Control and Telemetry Management System: authentication/RBAC, satellite lifecycle, telemetry, configurable thresholds, alerts, missions, commands, ground-station assignment and audit history.

## Approach
Unit tests are the largest layer; integration tests cover service/repository/API interactions; Selenium Page Objects cover a small number of end-to-end user journeys. Formal design uses equivalence partitioning, boundary-value analysis, decision tables and state-transition testing. UAT scenarios are defined for operator, engineer and viewer workflows.

## Entry criteria
Build compiles; database can initialize; seeded test users exist; test environment is available; critical requirements are baselined.

## Exit criteria
Core business logic meets the agreed branch-coverage target; all critical/high defects are closed or explicitly accepted; CI is green; required Selenium and integration journeys pass; residual risks are documented.

## Risk priority
Highest: telemetry threshold logic, authorization, alert lifecycle, mission state transitions and command authorization. Medium: filtering/history/audit UI. Lower: presentation-only formatting.

## Schedule
Week 1: working application, plan and formal test design. Week 2: unit/integration/Selenium tests, GitHub Actions, Jenkins and coverage. Week 3: regression demonstration, defect log, metrics and final reports.
