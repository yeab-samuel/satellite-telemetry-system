package com.aau.satellite.system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

@Tag("system")
@DisplayName("Satellites Page Tests")
class SatellitesTests extends BaseTest {

  private SatellitesPage openSatellitesPage() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    loginPage.loginAsOperator();
    SatellitesPage satellitesPage = new SatellitesPage(driver, UserRole.OPERATOR);
    satellitesPage.open();
    return satellitesPage;
  }

  @Test
  @DisplayName("Satellites page loads with search and filter")
  void satellitesPage_loadsWithFilters() {
    SatellitesPage satellitesPage = openSatellitesPage();

    assertTrue(satellitesPage.isSearchFieldDisplayed());
    assertTrue(satellitesPage.isStatusDropdownDisplayed());
    assertTrue(satellitesPage.isApplyFiltersButtonDisplayed());
    assertTrue(satellitesPage.isResetButtonDisplayed());
  }

  @Test
  @DisplayName("Can search satellites by name or code")
  void satellitesPage_filtersBySearch() {
    SatellitesPage satellitesPage = openSatellitesPage();

    satellitesPage.searchSatellites("Abyssinia");

    assertTrue(satellitesPage.isFilterApplied("q=Abyssinia"));
  }

  @Test
  @DisplayName("Can filter satellites by status")
  void satellitesPage_filtersByStatus() {
    SatellitesPage satellitesPage = openSatellitesPage();

    satellitesPage.filterByStatus("OPERATIONAL");

    assertTrue(satellitesPage.isFilterApplied("status=OPERATIONAL"));
  }

  @Test
  @DisplayName("Reset button clears all filters")
  void satellitesPage_resetFilters() {
    SatellitesPage satellitesPage = openSatellitesPage();

    satellitesPage.searchSatellites("SAT-001");
    satellitesPage.resetFilters();

    assertFalse(driver.getCurrentUrl().contains("q="));
  }

  @Test
  @DisplayName("Clicking satellite link navigates to detail")
  void satellitesPage_satelliteLinks_goToDetail() {
    SatellitesPage satellitesPage = openSatellitesPage();
    assertTrue(satellitesPage.hasSatellites(), "Expected at least one seeded satellite");

    satellitesPage.clickFirstSatellite();

    assertTrue(driver.getCurrentUrl().matches(".*/satellites/\\d+$"));
  }

  @Test
  @DisplayName("Empty search results show appropriate message")
  void satellitesPage_emptyResults() {
    SatellitesPage satellitesPage = openSatellitesPage();

    satellitesPage.searchSatellites("NONEXISTENT_SATELLITE_XYZ");

    assertTrue(satellitesPage.getEmptyMessage().contains("No satellites match"));
  }
}
