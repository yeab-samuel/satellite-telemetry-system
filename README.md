# SAT-MC — Satellite Mission Control & Telemetry Management

A Spring Boot 3 application created for the Addis Ababa University **Software Testing and Validation** final project. The system is intentionally small but contains business rules, role-based access control, state transitions, telemetry validation, a web UI, API endpoints, and automated tests.

The implementation follows the assignment shape: unit tests, integration tests, Selenium system tests with a Page Object, JaCoCo coverage, GitHub Actions, Jenkins, formal test-design targets, defect management, and metrics.

## 1. Technology

- Java 17+
- Spring Boot 3.2
- Spring MVC + Thymeleaf
- Spring Security
- Spring Data JPA
- H2 file database for the demo
- JUnit 5 + Mockito
- Selenium 4
- JaCoCo
- Maven

## 2. Run the application

### Option A — Maven

```bash
mvn clean test
mvn spring-boot:run
```

Then open:

```text
http://localhost:8080/login
```

### Option B — Build a JAR

```bash
mvn clean package
java -jar target/satellite-telemetry-system-1.0.0.jar
```

## 3. Demo accounts

The application seeds four accounts at startup:

| Role | Username | Password |
|---|---|---|
| Administrator | `admin` | `admin123` |
| Operator | `operator` | `operator123` |
| Engineer | `engineer` | `engineer123` |
| Viewer | `viewer` | `viewer123` |

For a reliable classroom/demo environment, the seeder re-applies these demo passwords on every application start. This prevents an old local database from leaving the supplied login credentials unusable.

## 4. Important login fix

The custom Spring Security login page now includes the required CSRF token. Static assets such as `/app.css` are also explicitly public.

These two details were important because:

1. A custom login form without the CSRF field can be rejected with **403 Forbidden**.
2. If the stylesheet is protected by authentication, the login page can appear unstyled and confusing.

Logout and every state-changing HTML form also submit the CSRF token.

## 5. Application modules

- Authentication and RBAC: ADMIN, OPERATOR, ENGINEER, VIEWER
- Satellite lifecycle and ground-station assignment
- Telemetry ingestion and anomaly detection
- Configurable telemetry thresholds
- Alert acknowledgement, escalation and resolution
- Mission creation and state transitions
- Satellite command authorization and history
- Search/filtering and telemetry history
- Audit trail

## 6. Test structure

The repository follows the test pyramid:

```text
src/test/java/
├── com/aau/satellite/
│   ├── service/       # fast unit tests
│   ├── web/           # Spring integration tests
│   └── system/        # Selenium Page Object system tests
```

Run the unit and integration tests only (fast, no browser, no server needed):

```bash
mvn clean test
```

Generate/open coverage:

```text
target/site/jacoco/index.html
```

Run **everything**, including the Selenium system tests:

```bash
mvn clean verify
```

`verify` automatically starts the application (against an in-memory database, see
`application-it.yml`), runs every test tagged `system` through Maven Failsafe, then stops the
application again. You do not need to start the app yourself or run the system tests separately.

To skip the Selenium suite on a machine without Chrome installed:

```bash
mvn clean verify -DskipSystemTests=true
```

## 7. Selenium system tests

The system tests live in `src/test/java/com/aau/satellite/system/`, one test class per page
(`LoginSystemTest`, `DashboardTests`, `SatellitesTests`, `MissionsTests`,
`ConfigurationTests`, `NavigationTests`, `RoleBasedAccessTests`), each built on shared Page
Objects (`LoginPage`, `DashboardPage`, `SatellitesPage`, `MissionsPage`,
`SatelliteDetailPage`, `ConfigurationPage`, `ErrorPage`) and a common `BaseTest`/`BasePage`.

**Headless by design.** Chrome runs with `--headless=new`: no browser window or tab is ever
opened, the whole suite runs in the background. Because there is nothing to watch, results are
surfaced two other ways:

- A `PASS`/`FAIL` line is printed to the console for every system test as it finishes
  (`SystemTestReporter`).
- On any failure, a screenshot of the browser at the moment of failure is written to
  `target/selenium-screenshots/`.

The standard Maven and Failsafe reports are also produced as usual, under
`target/surefire-reports/` (unit/integration) and `target/failsafe-reports/` (system tests).

A representative login journey:

```text
Open /login
    ↓
Enter operator / operator123
    ↓
Submit the CSRF-protected login form
    ↓
Redirect to /dashboard
    ↓
Verify the dashboard stats grid is displayed
```

## 8. CI

GitHub Actions is defined in:

```text
.github/workflows/ci.yml
```

Jenkins is defined in:

```text
Jenkinsfile
```

Both pipelines run `mvn -B clean verify` (not just `test`), so unit, integration, **and**
headless Selenium system tests all run on every push, and both publish the surefire/failsafe
reports, the JaCoCo report, and any Selenium failure screenshots as build artifacts.

The Docker setup is in:

```text
Dockerfile
docker-compose.yml
```

GitHub Actions runs the full Maven suite (`mvn clean verify`, including the headless Selenium
system tests) and uploads the JaCoCo report, the surefire/failsafe test reports, and any Selenium
failure screenshots as build artifacts.

## 9. Formal testing targets

The project contains explicit targets for:

- Equivalence partitioning
- Boundary value analysis
- Decision tables
- State transition testing
- Test doubles
- Test pyramid
- Selenium + Page Object
- Regression testing
- Continuous integration
- Defect management
- Coverage
- Test metrics

See `docs/TEST_DESIGN.md` and `docs/TEST_PLAN.md`.

## 10. Troubleshooting

### Login keeps failing

1. Stop the application.
2. Start it again with `mvn spring-boot:run`.
3. Use exactly `operator` / `operator123`.
4. If the local database is corrupted, remove the generated `data/` directory and start again.

The `data/` directory is local runtime data and is ignored by Git.

### Login page has no styling

Make sure the application is being opened at `http://localhost:8080/login`. The stylesheet is `/app.css` and is deliberately public.

### Selenium system tests fail to start a browser

Selenium 4.6+ uses Selenium Manager, which resolves a matching `chromedriver` automatically as
long as Google Chrome is installed on the machine/runner and there is internet access the first
time it runs. If Chrome isn't available (e.g. a minimal container), run
`mvn clean verify -DskipSystemTests=true` to skip just the Selenium suite.

### A POST action returns 403

All application forms now contain the Spring Security CSRF token. If you add a new POST/PUT/DELETE HTML form, include the same hidden token pattern.

## 11. Scope

This is a simulator. It does not implement real spacecraft hardware, radio communication, or external identity providers. The simplified scope keeps the application suitable for the course's testing objectives.

## 12. Academic project note

The supplied course brief says the application is the vehicle and the **testing effort is the point**. The repository therefore keeps the application bounded while providing evidence points for the required testing techniques and automation.
