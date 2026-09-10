package com.aau.satellite.system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

@Tag("system")
@DisplayName("Dashboard Functionality Tests")
class DashboardTests extends BaseTest {

  @Test
  @DisplayName("Dashboard displays all statistics cards")
  void dashboard_displaysSatelliteStats() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    DashboardPage dashboard = loginPage.loginAsOperator();

    assertEquals(3, dashboard.getStatCardsCount());
    assertTrue(dashboard.isFleetTableDisplayed());
  }

  @Test
  @DisplayName("Dashboard fleet status links to satellites page")
  void dashboard_fleetStatus_linksToSatellites() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    DashboardPage dashboard = loginPage.loginAsOperator();

    SatellitesPage satellitesPage = dashboard.clickViewFleet();

    assertTrue(satellitesPage.isSatellitesPageLoaded());
  }

  @Test
  @DisplayName("Dashboard satellite links navigate to detail page")
  void dashboard_satelliteLinks_navigateToDetail() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    DashboardPage dashboard = loginPage.loginAsOperator();

    if (dashboard.hasSatellites()) {
      SatelliteDetailPage detailPage = dashboard.clickFirstSatellite();
      assertTrue(detailPage.isSatelliteDetailLoaded());
    }
  }

  @Test
  @DisplayName("Dashboard stats have valid values")
  void dashboard_statsHaveValidValues() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    DashboardPage dashboard = loginPage.loginAsOperator();

    assertNotNull(dashboard.getSatellitesStat());
    assertNotNull(dashboard.getAlertsStat());
    assertNotNull(dashboard.getMissionsStat());
  }
}
