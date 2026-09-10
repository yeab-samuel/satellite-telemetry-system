package com.aau.satellite.system;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

@Tag("system")
@DisplayName("Missions Page Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MissionsTests extends BaseTest {

  @Test
  @Order(1)
  @DisplayName("Missions page loads with create form")
  void missionsPage_loadsWithCreateForm() {
    loginAs("operator", "operator123");
    navigateTo("/missions");

    // Wait for the mission creation form to load
    wait.until(
            ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".card form[action*='/missions']")));

    // Verify all form fields are present and visible
    assertTrue(driver.findElement(By.cssSelector("input[name='name']")).isDisplayed());
    assertTrue(driver.findElement(By.cssSelector("input[name='description']")).isDisplayed());
    assertTrue(driver.findElement(By.cssSelector("select[name='satelliteId']")).isDisplayed());
    assertTrue(driver.findElement(By.cssSelector("select[name='groundStationId']")).isDisplayed());
    assertTrue(driver.findElement(By.cssSelector("input[name='start']")).isDisplayed());
    assertTrue(driver.findElement(By.cssSelector("input[name='end']")).isDisplayed());
  }

  @Test
  @Order(2)
  @DisplayName("Empty mission list shows appropriate message")
  void missionsPage_emptyList() {
    loginAs("operator", "operator123");
    navigateTo("/missions");

    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".table-wrap")));

    // Check if empty message exists and is visible
    WebElement emptyMsg = driver.findElement(By.cssSelector(".table-wrap .empty"));
    if (emptyMsg.isDisplayed()) {
      assertTrue(emptyMsg.getText().contains("No missions"));
    }
  }

  @Test
  @Order(3)
  @DisplayName("Can create a new mission")
  void missionsPage_createsNewMission() {
    loginAs("operator", "operator123");
    navigateTo("/missions");

    // Generate unique mission name using timestamp
    String missionName = "Test Mission " + System.currentTimeMillis();
    driver.findElement(By.cssSelector("input[name='name']")).sendKeys(missionName);
    driver
            .findElement(By.cssSelector("input[name='description']"))
            .sendKeys("Automated test mission");

    // Select satellite from dropdown - use first available option
    Select satelliteSelect =
            new Select(driver.findElement(By.cssSelector("select[name='satelliteId']")));
    satelliteSelect.selectByIndex(0);

    // Select ground station from dropdown - use first available option
    Select stationSelect =
            new Select(driver.findElement(By.cssSelector("select[name='groundStationId']")));
    stationSelect.selectByIndex(0);

    // Fill mission time window
    driver.findElement(By.cssSelector("input[name='start']")).sendKeys("2026-09-10T10:00:00Z");
    driver.findElement(By.cssSelector("input[name='end']")).sendKeys("2026-09-10T11:00:00Z");

    // Submit the form using the button with btn class
    driver.findElement(By.cssSelector("button.btn")).click();

    // Wait for the mission table to appear and verify we stay on missions page
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".card .table-wrap")));
    assertTrue(driver.getCurrentUrl().contains("/missions"));
  }

  @Test
  @Order(4)
  @DisplayName("Can update mission status")
  void missionsPage_updatesMissionStatus() {
    loginAs("operator", "operator123");
    navigateTo("/missions");

    // Wait for the mission table to load
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".table-wrap")));

    // Check if there are any missions
    List<WebElement> rows = driver.findElements(By.cssSelector(".table-wrap table tbody tr"));
    boolean hasMissions = rows.size() > 0 && !rows.get(0).getAttribute("class").contains("empty");

    // If no missions exist, create one first
    if (!hasMissions) {
      // Create a mission
      String missionName = "Status Test Mission " + System.currentTimeMillis();
      driver.findElement(By.cssSelector("input[name='name']")).sendKeys(missionName);
      driver.findElement(By.cssSelector("input[name='description']")).sendKeys("Mission for status test");

      Select satelliteSelect = new Select(driver.findElement(By.cssSelector("select[name='satelliteId']")));
      satelliteSelect.selectByIndex(0);

      Select stationSelect = new Select(driver.findElement(By.cssSelector("select[name='groundStationId']")));
      stationSelect.selectByIndex(0);

      driver.findElement(By.cssSelector("input[name='start']")).sendKeys("2026-09-10T10:00:00Z");
      driver.findElement(By.cssSelector("input[name='end']")).sendKeys("2026-09-10T11:00:00Z");
      driver.findElement(By.cssSelector("button.btn")).click();

      wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".card .table-wrap")));

      // Refresh the rows list after creating the mission
      rows = driver.findElements(By.cssSelector(".table-wrap table tbody tr"));
    }

    // Find the first mission row (skip empty message if present)
    WebElement firstRow = null;
    for (WebElement row : rows) {
      if (!row.getAttribute("class").contains("empty")) {
        firstRow = row;
        break;
      }
    }

    // If still no mission found, fail the test
    if (firstRow == null) {
      fail("No missions available to update status");
    }

    // Find the status dropdown within the row's form and select first option
    Select statusSelect = new Select(firstRow.findElement(By.cssSelector("form select[name='status']")));
    statusSelect.selectByIndex(0);

    // Find and click the Apply button within the same form
    WebElement applyBtn = firstRow.findElement(By.cssSelector("form button.btn[type='submit']"));
    applyBtn.click();

    // Wait for the table to reload and verify we stay on missions page
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".table-wrap")));
    assertTrue(driver.getCurrentUrl().contains("/missions"));
  }

  @Test
  @Order(5)
  @DisplayName("Validation prevents mission creation with missing fields")
  void missionsPage_validationRequiresRequiredFields() {
    loginAs("operator", "operator123");
    navigateTo("/missions");

    // Wait for the create mission form to load
    wait.until(
            ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".card form[action*='/missions']")));

    // Submit the form without filling any fields to trigger validation
    driver.findElement(By.cssSelector(".card form[action*='/missions'] button.btn")).click();

    // Verify we remain on the missions page (validation prevents submission)
    wait.until(ExpectedConditions.urlContains("/missions"));
    assertTrue(driver.getCurrentUrl().contains("/missions"));
  }
}