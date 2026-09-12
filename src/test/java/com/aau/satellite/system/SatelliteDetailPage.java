package com.aau.satellite.system;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SatelliteDetailPage extends BasePage {

  private final BaseTest.UserRole currentUserRole;

  // Locators. Scoped to what actually exists in satellite-detail.html: the page
  // has no unique wrapper class, so we use the lifecycle transition form's
  // action attribute (unique to this page) as the "are we on the right page"
  // signal, the page-header <h1> for the name, and the first "muted-box" KPI
  // (Code is always listed first) for the code.
  private final By satelliteName = By.cssSelector(".page-header h1");
  private final By satelliteCode = By.cssSelector(".grid .muted-box strong");
  private final By statusBadge = By.cssSelector(".badge");
  private final By detailContainer = By.cssSelector("form[action*='/state']");
  private final By configurationLink = By.cssSelector("a[href*='/configuration/']");
  private final By backToSatellitesLink = By.cssSelector("a[href='/satellites']");

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
    WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(configurationLink));
    String href = link.getAttribute("href");
    driver.get(href);
    wait.until(ExpectedConditions.urlContains("/configuration/"));
    return new ConfigurationPage(driver, currentUserRole);
  }

  public SatellitesPage clickBackToSatellites() {
    clickAndWaitForUrl(backToSatellitesLink, "/satellites");
    return new SatellitesPage(driver, currentUserRole);
  }

  public boolean hasConfigurationLink() {
    return isDisplayed(configurationLink);
  }
}
