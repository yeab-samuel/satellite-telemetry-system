package com.aau.satellite.system;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

  protected final WebDriver driver;
  protected final WebDriverWait wait;
  protected static final String BASE_URL = "http://localhost:8080";

  // CI runners are consistently slower than local machines, so give them more
  // headroom. GitHub Actions automatically sets CI=true on every job; locally
  // this env var is unset, so local runs keep the original 15-second timeout.
  private static final Duration WAIT_TIMEOUT =
      Duration.ofSeconds("true".equalsIgnoreCase(System.getenv("CI")) ? 30 : 15);

  public BasePage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
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
   * navigation, retrying the click up to two more times if it doesn't seem to
   * have registered. Headless Chrome occasionally drops a click on a
   * freshly-rendered element, and CI runners need more headroom than local
   * machines; retrying is far more robust than failing the whole test over a
   * single missed click.
   */
  protected void clickAndWaitForUrl(By locator, String urlFragment) {
    TimeoutException lastFailure = null;
    for (int attempt = 0; attempt < 3; attempt++) {
      try {
        click(locator);
        wait.until(ExpectedConditions.urlContains(urlFragment));
        return;
      } catch (TimeoutException e) {
        lastFailure = e;
      }
    }
    throw lastFailure;
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
    clickAndWaitForUrl(
        By.cssSelector("form[action='/logout'] button[type='submit']"), "login");
  }
}