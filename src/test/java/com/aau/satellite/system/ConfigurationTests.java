package com.aau.satellite.system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

@Tag("system")
@DisplayName("Configuration Page Tests")
class ConfigurationTests extends BaseTest {

  private SatelliteDetailPage openFirstSatelliteDetail(DashboardPage dashboard) {
    SatellitesPage satellitesPage = dashboard.clickViewFleet();
    assertTrue(
        satellitesPage.hasSatellites(),
        "Expected at least one satellite to be available for configuration tests");
    return satellitesPage.clickFirstSatellite();
  }

  @Test
  @DisplayName("Engineer can open configuration page with all fields visible")
  void configurationPage_loadsWithAllFieldsForEngineer() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    DashboardPage dashboard = loginPage.loginAsEngineer();

    SatelliteDetailPage detailPage = openFirstSatelliteDetail(dashboard);
    assertTrue(detailPage.canConfigureSatellite());

    ConfigurationPage configurationPage = detailPage.clickConfigure();

    assertTrue(configurationPage.isConfigurationPageLoaded());
    assertTrue(configurationPage.isAllFieldsDisplayed());
  }

  @Test
  @DisplayName("Admin can update and save configuration values")
  void configurationPage_adminCanSaveConfiguration() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    DashboardPage dashboard = loginPage.loginAsAdmin();

    SatelliteDetailPage detailPage = openFirstSatelliteDetail(dashboard);
    ConfigurationPage configurationPage = detailPage.clickConfigure();

    assertTrue(configurationPage.canEditConfiguration());

    // Battery limits must stay within the app's validated range of 0-15V.
    configurationPage.saveConfigurationWithValues(30.0, -20.0, 60.0, 5.0, 12.0, -90.0, 5.0);

    // A successful save redirects to the satellite detail page (this is the
    // app's real, intended behaviour - not a bug), so that's what we confirm.
    assertTrue(driver.getCurrentUrl().contains("/satellites/"));
  }

  @Test
  @DisplayName("Operator does not see the configure link on satellite detail page")
  void configurationPage_operatorCannotSeeConfigureLink() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    DashboardPage dashboard = loginPage.loginAsOperator();

    SatelliteDetailPage detailPage = openFirstSatelliteDetail(dashboard);

    assertFalse(detailPage.canConfigureSatellite());
    assertFalse(detailPage.hasConfigurationLink());
  }

  @Test
  @DisplayName("Cancel on configuration page returns to satellite detail page")
  void configurationPage_cancelReturnsToSatelliteDetail() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    DashboardPage dashboard = loginPage.loginAsAdmin();

    SatelliteDetailPage detailPage = openFirstSatelliteDetail(dashboard);
    ConfigurationPage configurationPage = detailPage.clickConfigure();

    SatelliteDetailPage detailPageAgain = configurationPage.clickCancel();

    assertTrue(detailPageAgain.isSatelliteDetailLoaded());
  }
}
