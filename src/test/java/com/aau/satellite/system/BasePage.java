package com.aau.satellite.system;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

  protected final WebDriver driver;
  protected final WebDriverWait wait;
  protected static final String BASE_URL = "http://localhost:8080";

  public BasePage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
  }

  public void navigateTo(String url) {
    driver.get(url);
    waitForPageLoad();
  }

  protected void waitForPageLoad() {
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("main")));
  }

  protected void waitForElement(By locator) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  protected void waitForElementToBeClickable(By locator) {
    wait.until(ExpectedConditions.elementToBeClickable(locator));
  }

  protected void click(By locator) {
    waitForElementToBeClickable(locator);
    driver.findElement(locator).click();
  }

  /**
   * Clicks a link/button and waits for the URL to reflect the resulting
   * navigation, retrying the click once if the first attempt doesn't seem to
   * have registered. Headless Chrome occasionally drops the very first click
   * on a freshly-rendered element; retrying once is far more robust than
   * failing the whole test over a single missed click.
   */
  protected void clickAndWaitForUrl(By locator, String urlFragment) {
    click(locator);
    try {
      wait.until(ExpectedConditions.urlContains(urlFragment));
    } catch (TimeoutException firstAttemptFailed) {
      click(locator);
      wait.until(ExpectedConditions.urlContains(urlFragment));
    }
  }

  protected void sendKeys(By locator, String text) {
    waitForElement(locator);
    driver.findElement(locator).clear();
    driver.findElement(locator).sendKeys(text);
  }

  protected String getText(By locator) {
    waitForElement(locator);
    return driver.findElement(locator).getText();
  }

  protected boolean isDisplayed(By locator) {
    try {
      return driver.findElement(locator).isDisplayed();
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  protected boolean isElementPresent(By locator) {
    try {
      driver.findElement(locator);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  public void logout() {
    click(By.cssSelector("form[action='/logout'] button[type='submit']"));
    wait.until(ExpectedConditions.urlContains("login"));
  }
}
