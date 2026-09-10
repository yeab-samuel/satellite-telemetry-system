package com.aau.satellite.system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

@Tag("system")
@DisplayName("Role-Based Access Control Tests")
class RoleBasedAccessTests extends BaseTest {

  @Test
  @DisplayName("ADMIN has all permissions")
  void adminHasAllPermissions() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    DashboardPage dashboard = loginPage.loginAsAdmin();

    assertTrue(dashboard.canViewSatellites());
    assertTrue(dashboard.canCreateMissions());
    assertTrue(dashboard.canEditConfiguration());
    assertTrue(dashboard.canDeleteSatellites());
    assertTrue(dashboard.canSendCommands());
  }

  @Test
  @DisplayName("OPERATOR has limited permissions")
  void operatorHasLimitedPermissions() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    DashboardPage dashboard = loginPage.loginAsOperator();

    assertTrue(dashboard.canViewSatellites());
    assertTrue(dashboard.canCreateMissions());
    assertFalse(dashboard.canEditConfiguration());
    assertFalse(dashboard.canDeleteSatellites());
    assertTrue(dashboard.canSendCommands());
  }

  @Test
  @DisplayName("ENGINEER has configuration permissions")
  void engineerCanEditConfiguration() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    DashboardPage dashboard = loginPage.loginAsEngineer();

    assertTrue(dashboard.canViewSatellites());
    assertTrue(dashboard.canCreateMissions());
    assertTrue(dashboard.canEditConfiguration());
    assertFalse(dashboard.canDeleteSatellites());
    assertTrue(dashboard.canSendCommands());
  }

  @Test
  @DisplayName("VIEWER has read-only permissions")
  void viewerHasReadOnlyPermissions() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    DashboardPage dashboard = loginPage.loginAsViewer();

    assertTrue(dashboard.canViewSatellites());
    assertFalse(dashboard.canCreateMissions());
    assertFalse(dashboard.canEditConfiguration());
    assertFalse(dashboard.canDeleteSatellites());
    assertFalse(dashboard.canSendCommands());
  }

  @Test
  @DisplayName("Viewer cannot access configuration page")
  void viewerCannotAccessConfiguration() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    DashboardPage dashboard = loginPage.loginAsViewer();

    driver.get(BASE_URL + "/configuration/1");

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.contains("login")
            || currentUrl.contains("error")
            || currentUrl.contains("dashboard"));
  }
}
