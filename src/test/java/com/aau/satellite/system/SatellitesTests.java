package com.aau.satellite.system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

@Tag("system")
@DisplayName("Satellites Page Tests")
class SatellitesTests extends BaseTest {

  @Test
  @DisplayName("Satellites page loads with search and filter")
  void satellitesPage_loadsWithFilters() {
    loginAs("operator", "operator123");
    navigateTo("/satellites");

    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".card form")));

    assertTrue(driver.findElement(By.id("q")).isDisplayed());
    assertTrue(driver.findElement(By.id("status")).isDisplayed());
    assertTrue(driver.findElement(By.cssSelector("button[type='submit']")).isDisplayed());
    assertTrue(driver.findElement(By.cssSelector("a[href='/satellites']")).isDisplayed());
  }

  @Test
  @DisplayName("Can search satellites by name or code")
  void satellitesPage_filtersBySearch() {
    loginAs("operator", "operator123");
    navigateTo("/satellites");

    WebElement searchInput = driver.findElement(By.id("q"));
    searchInput.sendKeys("Abyssinia");
    driver.findElement(By.cssSelector("button[type='submit']")).click();

    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".table-wrap")));
    assertTrue(driver.getCurrentUrl().contains("q=Abyssinia"));
  }

  @Test
  @DisplayName("Can filter satellites by status")
  void satellitesPage_filtersByStatus() {
    loginAs("operator", "operator123");
    navigateTo("/satellites");

    Select statusSelect = new Select(driver.findElement(By.id("status")));
    statusSelect.selectByVisibleText("OPERATIONAL");
    driver.findElement(By.cssSelector("button[type='submit']")).click();

    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".table-wrap")));
    assertTrue(driver.getCurrentUrl().contains("status=OPERATIONAL"));
  }

  @Test
  @DisplayName("Reset button clears all filters")
  void satellitesPage_resetFilters() {
    loginAs("operator", "operator123");
    navigateTo("/satellites");

    WebElement searchInput = driver.findElement(By.id("q"));
    searchInput.sendKeys("SAT-001");
    driver.findElement(By.cssSelector("button[type='submit']")).click();

    driver.findElement(By.cssSelector("a[href='/satellites']")).click();

    wait.until(ExpectedConditions.urlToBe(BASE_URL + "/satellites"));
    assertFalse(driver.getCurrentUrl().contains("q="));
  }

  @Test
  @DisplayName("Clicking satellite link navigates to detail")
  void satellitesPage_satelliteLinks_goToDetail() {
    loginAs("operator", "operator123");
    navigateTo("/satellites");

    WebElement firstSatelliteLink =
        driver.findElement(
            By.cssSelector(".table-wrap table tbody tr:first-child td:first-child a"));
    firstSatelliteLink.click();

    wait.until(ExpectedConditions.urlContains("/satellites/"));
    assertTrue(driver.getCurrentUrl().matches(".*/satellites/\\d+$"));
  }

  @Test
  @DisplayName("Empty search results show appropriate message")
  void satellitesPage_emptyResults() {
    loginAs("operator", "operator123");
    navigateTo("/satellites");

    WebElement searchInput = driver.findElement(By.id("q"));
    searchInput.sendKeys("NONEXISTENT_SATELLITE_XYZ");
    driver.findElement(By.cssSelector("button[type='submit']")).click();

    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".table-wrap")));
    WebElement emptyMsg = driver.findElement(By.cssSelector(".empty"));
    assertTrue(emptyMsg.isDisplayed());
    assertTrue(emptyMsg.getText().contains("No satellites match"));
  }
}
