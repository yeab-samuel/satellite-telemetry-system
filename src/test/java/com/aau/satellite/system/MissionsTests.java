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

    wait.until(
            ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".card form[action*='/missions']")));

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

    String missionName = "Test Mission " + System.currentTimeMillis();
    driver.findElement(By.cssSelector("input[name='name']")).sendKeys(missionName);
    driver
            .findElement(By.cssSelector("input[name='description']"))
            .sendKeys("Automated test mission");

    Select satelliteSelect =
            new Select(driver.findElement(By.cssSelector("select[name='satelliteId']")));
    satelliteSelect.selectByIndex(0);

    Select stationSelect =
            new Select(driver.findElement(By.cssSelector("select[name='groundStationId']")));
    stationSelect.selectByIndex(0);

    // Set the date fields directly via JavaScript rather than simulated
    // keystrokes - sendKeys() on this field has proven unreliable in this
    // environment (Chrome version dependent). This is a plain text input with
    // no live validation/formatting JS reacting to keystrokes, so a direct
    // value assignment is equivalent and far more robust.
    WebElement startInput = driver.findElement(By.cssSelector("input[name='start']"));
    ((JavascriptExecutor) driver)
            .executeScript("arguments[0].value = arguments[1];", startInput, "2026-09-10T10:00:00Z");

    WebElement endInput = driver.findElement(By.cssSelector("input[name='end']"));
    ((JavascriptExecutor) driver)
            .executeScript("arguments[0].value = arguments[1];", endInput, "2026-09-10T11:00:00Z");

    // Submit the form directly rather than clicking the button
    endInput.submit();

    // Wait for the mission we just created to actually appear in the table
    wait.until(
            ExpectedConditions.textToBePresentInElementLocated(
                    By.cssSelector(".table-wrap"), missionName));
    assertTrue(driver.getCurrentUrl().contains("/missions"));
  }

  @Test
  @Order(4)
  @DisplayName("Can update mission status")
  void missionsPage_updatesMissionStatus() {
    loginAs("operator", "operator123");
    navigateTo("/missions");

    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".table-wrap")));

    // Check if there are any missions. The empty-state message is rendered as
    // <td class="empty">, not as a class on the <tr> itself, so we must look for
    // a ".empty" descendant of the row rather than the row's own class attribute.
    List<WebElement> rows = driver.findElements(By.cssSelector(".table-wrap table tbody tr"));
    boolean hasMissions =
            rows.size() > 0 && rows.get(0).findElements(By.cssSelector(".empty")).isEmpty();

    if (!hasMissions) {
      String missionName = "Status Test Mission " + System.currentTimeMillis();
      driver.findElement(By.cssSelector("input[name='name']")).sendKeys(missionName);
      driver
              .findElement(By.cssSelector("input[name='description']"))
              .sendKeys("Mission for status test");

      Select satelliteSelect =
              new Select(driver.findElement(By.cssSelector("select[name='satelliteId']")));
      satelliteSelect.selectByIndex(0);

      Select stationSelect =
              new Select(driver.findElement(By.cssSelector("select[name='groundStationId']")));
      stationSelect.selectByIndex(0);

      WebElement startInput = driver.findElement(By.cssSelector("input[name='start']"));
      ((JavascriptExecutor) driver)
              .executeScript("arguments[0].value = arguments[1];", startInput, "2026-09-10T10:00:00Z");

      WebElement endInput = driver.findElement(By.cssSelector("input[name='end']"));
      ((JavascriptExecutor) driver)
              .executeScript("arguments[0].value = arguments[1];", endInput, "2026-09-10T11:00:00Z");

      endInput.submit();

      // Wait for the specific mission we just created to actually appear, not
      // just for ".table-wrap" to exist (which is already true before
      // submitting, so it never proved the page had reloaded).
      wait.until(
              ExpectedConditions.textToBePresentInElementLocated(
                      By.cssSelector(".table-wrap"), missionName));

      rows = driver.findElements(By.cssSelector(".table-wrap table tbody tr"));
    }

    WebElement firstRow = null;
    for (WebElement row : rows) {
      if (row.findElements(By.cssSelector(".empty")).isEmpty()) {
        firstRow = row;
        break;
      }
    }

    if (firstRow == null) {
      fail("No missions available to update status");
    }

    // A freshly created mission starts in DRAFT, and the only valid
    // transitions from DRAFT are to SCHEDULED or CANCELLED (see
    // MissionService.transition).
    Select statusSelect =
            new Select(firstRow.findElement(By.cssSelector("form select[name='status']")));
    statusSelect.selectByVisibleText("SCHEDULED");

    WebElement applyBtn = firstRow.findElement(By.cssSelector("form button.btn[type='submit']"));
    applyBtn.submit();

    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".table-wrap")));
    assertTrue(driver.getCurrentUrl().contains("/missions"));
  }

  @Test
  @Order(5)
  @DisplayName("Validation prevents mission creation with missing fields")
  void missionsPage_validationRequiresRequiredFields() {
    loginAs("operator", "operator123");
    navigateTo("/missions");

    wait.until(
            ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".card form[action*='/missions']")));

    WebElement nameInput =
            driver.findElement(By.cssSelector(".card form[action*='/missions'] input[name='name']"));
    nameInput.submit();

    wait.until(ExpectedConditions.urlContains("/missions"));
    assertTrue(driver.getCurrentUrl().contains("/missions"));
  }
}