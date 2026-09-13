package com.aau.satellite.system;

import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DashboardPage extends BasePage {

  private final BaseTest.UserRole currentUserRole;

  // Locators
  private final By pageHeader = By.cssSelector(".page-header h1");
  private final By statsGrid = By.cssSelector(".stats-grid");
  private final By statCards = By.cssSelector(".stat-card");
  private final By statValues = By.cssSelector(".stat-card .stat-value");
  private final By fleetTable = By.cssSelector(".card .table-wrap table");
  private final By fleetTableRows = By.cssSelector(".card .table-wrap table tbody tr");
  // Scoped to .page-header to avoid matching the topbar's own "Satellites"
  // nav link, which shares the same href and would otherwise be matched first.
  private final By viewFleetButton = By.cssSelector(".page-header a[href='/satellites']");
  private final By satelliteLinks = By.cssSelector(".table-wrap table tbody tr td:first-child a");
  private final By emptyMessage = By.cssSelector(".empty");

  public DashboardPage(WebDriver driver, BaseTest.UserRole role) {
    super(driver);
    this.currentUserRole = role;
  }

  public DashboardPage(WebDriver driver) {
    this(driver, BaseTest.UserRole.OPERATOR);
  }

  public void open() {
    navigateTo(BASE_URL + "/dashboard");
    waitForElement(pageHeader);
  }

  public boolean isDashboardLoaded() {
    return isDisplayed(pageHeader) && getPageTitle().contains("Dashboard");
  }

  public String getPageTitle() {
    return getText(pageHeader);
  }

  public int getStatCardsCount() {
    return driver.findElements(statCards).size();
  }

  public String getSatellitesStat() {
    List<WebElement> stats = driver.findElements(statValues);
    return stats.size() > 0 ? stats.get(0).getText() : "";
  }

  public String getAlertsStat() {
    List<WebElement> stats = driver.findElements(statValues);
    return stats.size() > 1 ? stats.get(1).getText() : "";
  }

  public String getMissionsStat() {
    List<WebElement> stats = driver.findElements(statValues);
    return stats.size() > 2 ? stats.get(2).getText() : "";
  }

  public boolean isStatsGridDisplayed() {
    return isDisplayed(statsGrid);
  }

  public boolean isFleetTableDisplayed() {
    return isDisplayed(fleetTable);
  }

  public int getFleetRowCount() {
    return driver.findElements(fleetTableRows).size();
  }

  public SatellitesPage clickViewFleet() {
    clickAndWaitForUrl(viewFleetButton, "satellites");
    return new SatellitesPage(driver, currentUserRole);
  }

  public SatelliteDetailPage clickFirstSatellite() {
    WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(satelliteLinks));
    String href = link.getAttribute("href");
    driver.get(href);
    wait.until(ExpectedConditions.urlContains("/satellites/"));
    return new SatelliteDetailPage(driver, currentUserRole);
  }

  public String getFirstSatelliteCode() {
    List<WebElement> links = driver.findElements(satelliteLinks);
    return links.size() > 0 ? links.get(0).getText() : "";
  }

  public boolean hasSatellites() {
    return driver.findElements(fleetTableRows).size() > 0 && !isDisplayed(emptyMessage);
  }

  // Role-based permission checks
  public boolean canViewSatellites() {
    return true;
  }

  public boolean canCreateMissions() {
    return currentUserRole == BaseTest.UserRole.ADMIN
        || currentUserRole == BaseTest.UserRole.OPERATOR
        || currentUserRole == BaseTest.UserRole.ENGINEER;
  }

  public boolean canEditConfiguration() {
    return currentUserRole == BaseTest.UserRole.ADMIN
        || currentUserRole == BaseTest.UserRole.ENGINEER;
  }

  public boolean canDeleteSatellites() {
    return currentUserRole == BaseTest.UserRole.ADMIN;
  }

  public boolean canSendCommands() {
    return currentUserRole == BaseTest.UserRole.ADMIN
        || currentUserRole == BaseTest.UserRole.OPERATOR
        || currentUserRole == BaseTest.UserRole.ENGINEER;
  }
}