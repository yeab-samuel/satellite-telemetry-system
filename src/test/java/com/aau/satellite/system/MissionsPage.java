package com.aau.satellite.system;

import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class MissionsPage extends BasePage {

  private final BaseTest.UserRole currentUserRole;

  // Locators - Create Mission Form
  private final By missionNameInput = By.cssSelector("input[name='name']");
  private final By descriptionInput = By.cssSelector("input[name='description']");
  private final By satelliteSelect = By.cssSelector("select[name='satelliteId']");
  private final By groundStationSelect = By.cssSelector("select[name='groundStationId']");
  private final By startInput = By.cssSelector("input[name='start']");
  private final By endInput = By.cssSelector("input[name='end']");
  private final By createMissionButton = By.cssSelector(".card:first-child button[type='submit']");
  private final By createMissionForm = By.cssSelector(".card:first-child form");

  // Locators - Mission Queue
  private final By missionTable = By.cssSelector(".table-wrap table");
  private final By missionRows = By.cssSelector(".table-wrap table tbody tr");
  private final By emptyMessage = By.cssSelector(".empty");

  public MissionsPage(WebDriver driver, BaseTest.UserRole role) {
    super(driver);
    this.currentUserRole = role;
  }

  public MissionsPage(WebDriver driver) {
    this(driver, BaseTest.UserRole.OPERATOR);
  }

  public void open() {
    navigateTo(BASE_URL + "/missions");
    waitForElement(missionTable);
  }

  public boolean isMissionsPageLoaded() {
    return isDisplayed(missionTable);
  }

  public boolean canCreateMission() {
    return currentUserRole == BaseTest.UserRole.ADMIN
        || currentUserRole == BaseTest.UserRole.OPERATOR
        || currentUserRole == BaseTest.UserRole.ENGINEER;
  }

  public boolean canUpdateMissionStatus() {
    return currentUserRole == BaseTest.UserRole.ADMIN
        || currentUserRole == BaseTest.UserRole.OPERATOR
        || currentUserRole == BaseTest.UserRole.ENGINEER;
  }

  public void createMission(String name, String description, String startTime, String endTime) {
    if (!canCreateMission()) {
      throw new SecurityException("User role " + currentUserRole + " cannot create missions");
    }
    sendKeys(missionNameInput, name);
    sendKeys(descriptionInput, description);
    Select satelliteSelect = new Select(driver.findElement(this.satelliteSelect));
    satelliteSelect.selectByIndex(1);
    Select stationSelect = new Select(driver.findElement(groundStationSelect));
    stationSelect.selectByIndex(1);
    sendKeys(startInput, startTime);
    sendKeys(endInput, endTime);
    click(createMissionButton);
    wait.until(ExpectedConditions.presenceOfElementLocated(missionTable));
  }

  public int getMissionCount() {
    List<WebElement> rows = driver.findElements(missionRows);
    if (isDisplayed(emptyMessage)) {
      return 0;
    }
    return rows.size();
  }

  public boolean hasMissions() {
    return driver.findElements(missionRows).size() > 0 && !isDisplayed(emptyMessage);
  }

  public String getFirstMissionStatus() {
    WebElement firstRow =
        driver.findElement(By.cssSelector(".table-wrap table tbody tr:first-child"));
    return firstRow.findElement(By.cssSelector(".badge")).getText();
  }

  public void updateFirstMissionStatus(String status) {
    if (!canUpdateMissionStatus()) {
      throw new SecurityException("User role " + currentUserRole + " cannot update mission status");
    }
    WebElement firstRow =
        driver.findElement(By.cssSelector(".table-wrap table tbody tr:first-child"));
    Select statusSelect = new Select(firstRow.findElement(By.cssSelector("select[name='status']")));
    statusSelect.selectByVisibleText(status);
    firstRow.findElement(By.cssSelector("button[type='submit']")).click();
    wait.until(ExpectedConditions.presenceOfElementLocated(missionTable));
  }

  public boolean isCreateMissionFormDisplayed() {
    return isDisplayed(createMissionForm);
  }
}
