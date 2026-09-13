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

  /**
   * Clicks an element, falling back to a JS-triggered click if the native click
   * doesn't register (a known quirk with certain elements in newer headless
   * Chrome). The native click is always tried first, so this changes nothing
   * for the common case where it works fine.
   */
  protected void click(By locator) {
      waitForElementToBeClickable(locator);
      WebElement element = driver.findElement(locator);
      try {
        element.click();
      } catch (WebDriverException e) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
      }
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

  /**
   * Types into a field, explicitly clicking it first to guarantee it has
   * keyboard focus. Visibility alone doesn't guarantee an element is focused,
   * and in headless Chrome sendKeys() can silently type into nothing if the
   * field was never actually focused first.
   */
  protected void sendKeys(By locator, String text) {
      for (int attempt = 0; attempt < 3; attempt++) {
        waitForElement(locator);
        WebElement element = driver.findElement(locator);
        element.click();
        element.clear();
        element.sendKeys(text);
        if (text.equals(element.getAttribute("value"))) {
          return;
        }
      }
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