package com.aau.satellite.system;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ConfigurationPage extends BasePage {

  private final BaseTest.UserRole currentUserRole;

  // Locators
  private final By intervalInput = By.cssSelector("input[name='interval']");
  private final By tempMinInput = By.cssSelector("input[name='tempMin']");
  private final By tempMaxInput = By.cssSelector("input[name='tempMax']");
  private final By batteryMinInput = By.cssSelector("input[name='batteryMin']");
  private final By batteryMaxInput = By.cssSelector("input[name='batteryMax']");
  private final By signalMinInput = By.cssSelector("input[name='signalMin']");
  private final By packetLossMaxInput = By.cssSelector("input[name='packetLossMax']");
  private final By saveButton = By.cssSelector("main form button[type='submit']");
  private final By cancelButton = By.cssSelector("a[href*='/satellites/']");
  private final By configForm = By.cssSelector(".card form");

  public ConfigurationPage(WebDriver driver, BaseTest.UserRole role) {
    super(driver);
    this.currentUserRole = role;
  }

  public ConfigurationPage(WebDriver driver) {
    this(driver, BaseTest.UserRole.OPERATOR);
  }

  public void open(String satelliteId) {
    navigateTo(BASE_URL + "/configuration/" + satelliteId);
    waitForElement(configForm);
  }

  public boolean isConfigurationPageLoaded() {
    return isDisplayed(intervalInput) && isDisplayed(saveButton);
  }

  public boolean canEditConfiguration() {
    return currentUserRole == BaseTest.UserRole.ADMIN
        || currentUserRole == BaseTest.UserRole.ENGINEER;
  }

  public void setInterval(double interval) {
    if (!canEditConfiguration()) {
      throw new SecurityException("User role " + currentUserRole + " cannot edit configuration");
    }
    sendKeys(intervalInput, String.valueOf(interval));
  }

  public void setTempMin(double tempMin) {
    if (!canEditConfiguration()) {
      throw new SecurityException("User role " + currentUserRole + " cannot edit configuration");
    }
    sendKeys(tempMinInput, String.valueOf(tempMin));
  }

  public void setTempMax(double tempMax) {
    if (!canEditConfiguration()) {
      throw new SecurityException("User role " + currentUserRole + " cannot edit configuration");
    }
    sendKeys(tempMaxInput, String.valueOf(tempMax));
  }

  public void setBatteryMin(double batteryMin) {
    if (!canEditConfiguration()) {
      throw new SecurityException("User role " + currentUserRole + " cannot edit configuration");
    }
    sendKeys(batteryMinInput, String.valueOf(batteryMin));
  }

  public void setBatteryMax(double batteryMax) {
    if (!canEditConfiguration()) {
      throw new SecurityException("User role " + currentUserRole + " cannot edit configuration");
    }
    sendKeys(batteryMaxInput, String.valueOf(batteryMax));
  }

  public void setSignalMin(double signalMin) {
    if (!canEditConfiguration()) {
      throw new SecurityException("User role " + currentUserRole + " cannot edit configuration");
    }
    sendKeys(signalMinInput, String.valueOf(signalMin));
  }

  public void setPacketLossMax(double packetLossMax) {
    if (!canEditConfiguration()) {
      throw new SecurityException("User role " + currentUserRole + " cannot edit configuration");
    }
    sendKeys(packetLossMaxInput, String.valueOf(packetLossMax));
  }

  public void saveConfiguration() {
    if (!canEditConfiguration()) {
      throw new SecurityException("User role " + currentUserRole + " cannot edit configuration");
    }
    // A successful save redirects to the satellite detail page (see
    // WebControllers#updateCfg) - that is the real signal to wait for, rather
    // than ".card form", which could also match an unrelated form on
    // whatever page we land on.
    clickAndWaitForUrl(saveButton, "/satellites/");
  }

  public void saveConfigurationWithValues(
      double interval,
      double tempMin,
      double tempMax,
      double batteryMin,
      double batteryMax,
      double signalMin,
      double packetLossMax) {
    setInterval(interval);
    setTempMin(tempMin);
    setTempMax(tempMax);
    setBatteryMin(batteryMin);
    setBatteryMax(batteryMax);
    setSignalMin(signalMin);
    setPacketLossMax(packetLossMax);
    saveConfiguration();
  }

  public SatelliteDetailPage clickCancel() {
    click(cancelButton);
    wait.until(ExpectedConditions.urlContains("/satellites/"));
    return new SatelliteDetailPage(driver, currentUserRole);
  }

  public boolean isAllFieldsDisplayed() {
    return isDisplayed(intervalInput)
        && isDisplayed(tempMinInput)
        && isDisplayed(tempMaxInput)
        && isDisplayed(batteryMinInput)
        && isDisplayed(batteryMaxInput)
        && isDisplayed(signalMinInput)
        && isDisplayed(packetLossMaxInput);
  }
}
