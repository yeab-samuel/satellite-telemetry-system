package com.aau.satellite.system;

import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class SatellitesPage extends BasePage {

  private final BaseTest.UserRole currentUserRole;

  // Locators
  private final By searchInput = By.id("q");
  private final By statusDropdown = By.id("status");
  private final By applyFiltersButton = By.cssSelector("main form button[type='submit']");
  private final By resetButton = By.cssSelector("a[href='/satellites']");
  private final By tableWrap = By.cssSelector(".table-wrap");
  private final By tableRows = By.cssSelector(".table-wrap table tbody tr");
  private final By satelliteLinks = By.cssSelector(".table-wrap table tbody tr td:first-child a");
  private final By emptyMessage = By.cssSelector(".empty");
  private final By resultCount = By.cssSelector(".card-title .small");

  public SatellitesPage(WebDriver driver, BaseTest.UserRole role) {
    super(driver);
    this.currentUserRole = role;
  }

  public SatellitesPage(WebDriver driver) {
    this(driver, BaseTest.UserRole.OPERATOR);
  }

  public void open() {
    navigateTo(BASE_URL + "/satellites");
    waitForElement(tableWrap);
  }

  public boolean isSatellitesPageLoaded() {
    return isDisplayed(searchInput) && isDisplayed(statusDropdown);
  }

  public void searchSatellites(String searchTerm) {
    sendKeys(searchInput, searchTerm);
    // Make sure the browser has actually registered the typed value before
    // submitting — on slower/CI environments, submit() can otherwise fire
    // before the input's value has "settled", submitting an empty query.
    wait.until(ExpectedConditions.attributeToBe(searchInput, "value", searchTerm));
    driver.findElement(searchInput).submit();
    wait.until(ExpectedConditions.urlContains("q=" + searchTerm));
  }

  public void filterByStatus(String status) {
    WebElement dropdown = driver.findElement(statusDropdown);
    new Select(dropdown).selectByVisibleText(status);
    dropdown.submit();
    wait.until(ExpectedConditions.urlContains("status=" + status));
  }

  public void resetFilters() {
    WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(resetButton));
    driver.get(link.getAttribute("href"));
    wait.until(ExpectedConditions.urlToBe(BASE_URL + "/satellites"));
  }

  public int getSatelliteCount() {
    List<WebElement> rows = driver.findElements(tableRows);
    if (isDisplayed(emptyMessage)) {
      return 0;
    }
    return rows.size();
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

  public String getFirstSatelliteStatus() {
    WebElement firstRow =
        driver.findElement(By.cssSelector(".table-wrap table tbody tr:first-child"));
    WebElement statusBadge = firstRow.findElement(By.cssSelector(".badge"));
    return statusBadge.getText();
  }

  public boolean hasSatellites() {
    return driver.findElements(tableRows).size() > 0 && !isDisplayed(emptyMessage);
  }

  public String getEmptyMessage() {
    return getText(emptyMessage);
  }

  public String getResultCount() {
    return getText(resultCount);
  }

  public boolean isSearchFieldDisplayed() {
    return isDisplayed(searchInput);
  }

  public boolean isStatusDropdownDisplayed() {
    return isDisplayed(statusDropdown);
  }

  public boolean isApplyFiltersButtonDisplayed() {
    return isDisplayed(applyFiltersButton);
  }

  public boolean isResetButtonDisplayed() {
    return isDisplayed(resetButton);
  }

  public boolean isFilterApplied(String filterParam) {
    return driver.getCurrentUrl().contains(filterParam);
  }

  public boolean canDeleteSatellites() {
    return currentUserRole == BaseTest.UserRole.ADMIN;
  }
}