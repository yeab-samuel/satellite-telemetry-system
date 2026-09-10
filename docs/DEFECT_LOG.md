# Defect Log

| ID | Steps to reproduce | Expected | Actual | Severity | Priority | Status |
|---|---|---|---|---|---|---|
| DEF-001 | Submit a telemetry value at a threshold boundary | Boundary is classified according to rule | Record observed result | High | High | New |
| DEF-002 | Login as VIEWER and submit a configuration change | Access denied | Record observed result | Critical | High | New |
| DEF-003 | Attempt an invalid mission transition | Transition rejected | Record observed result | High | High | New |
| DEF-004 | Run `mvn clean test`, then check which test classes actually executed | All automated tests, including the seven Selenium system-test classes, run and are reported on | `pom.xml` excluded the JUnit5 `system` tag from Surefire with no replacement execution, so every system test except `MissionsTests` was silently skipped on every build; `MissionsTests` (which was missing the `system` tag) did run, but failed on all 5 cases with `net::ERR_CONNECTION_REFUSED` because nothing in the pipeline started the application first. Chrome also ran with a visible window instead of headless. | Critical | High | Closed — fixed by adding a Maven Failsafe execution bound to `integration-test`, wiring Spring Boot's `start`/`stop` goals around it so the app is running before Selenium connects, tagging `MissionsTests` consistently, and switching Chrome to `--headless=new`. |

Update this file with actual defects found during execution. Do not invent defects for the final report.
