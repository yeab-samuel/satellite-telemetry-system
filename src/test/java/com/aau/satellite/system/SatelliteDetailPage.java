package com.aau.satellite.system;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SatelliteDetailPage extends BasePage {

  private final BaseTest.UserRole currentUserRole;

  // Locators
  private final By satelliteName = By.cssSelector(".page-header h1");
  // NOTE: relies on "Code" being the first .kpi block in satellite-detail.html's
  // identity grid (Code, Model, Orbit, Station, in that DOM order). If that grid's
  // order ever changes, this locator will start returning the wrong value.
  private final By satelliteCode = By.cssSelector(".muted-box .kpi strong");
  private final By statusBadge = By.cssSelector(".badge");
  private final By detailContainer = By.cssSelector(".page-header");
  private final By configurationLink = By.cssSelector("a[href*='/configuration/']");
  private final By backToSatellitesLink = By.cssSelector("a[href*='/satellites']");

  public SatelliteDetailPage(WebDriver driver, BaseTest.UserRole role) {
    super(driver);
    this.currentUserRole = role;
  }

  public SatelliteDetailPage(WebDriver driver) {
    this(driver, BaseTest.UserRole.OPERATOR);
  }

  public void open(String satelliteId) {
    navigateTo(BASE_URL + "/satellites/" + satelliteId);
    waitForElement(detailContainer);
  }

  public boolean isSatelliteDetailLoaded() {
    return isDisplayed(detailContainer);
  }

  public String getSatelliteName() {
    return getText(satelliteName);
  }

  public String getSatelliteCode() {
    return getText(satelliteCode);
  }

  public String getStatus() {
    return getText(statusBadge);
  }

  public boolean canConfigureSatellite() {
    return currentUserRole == BaseTest.UserRole.ADMIN
        || currentUserRole == BaseTest.UserRole.ENGINEER;
  }

  public ConfigurationPage clickConfigure() {
    if (!canConfigureSatellite()) {
      throw new SecurityException("User role " + currentUserRole + " cannot configure satellites");
    }
    click(configurationLink);
    wait.until(ExpectedConditions.urlContains("/configuration/"));
    return new ConfigurationPage(driver, currentUserRole);
  }

  public SatellitesPage clickBackToSatellites() {
    click(backToSatellitesLink);
    wait.until(ExpectedConditions.urlContains("/satellites"));
    return new SatellitesPage(driver, currentUserRole);
  }

  public boolean hasConfigurationLink() {
    return isDisplayed(configurationLink);
  }
}
