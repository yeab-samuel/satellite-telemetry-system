package com.aau.satellite.system;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

  protected final WebDriver driver;
  protected final WebDriverWait wait;
  protected static final String BASE_URL = "http://localhost:8080";
  private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(30);

  public BasePage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
  }

  public void navigateTo(String url) {
    driver.get(url);
    waitForDocumentReady();
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
   * Performs a deterministic Selenium click after scrolling the target into view.
   * Stale references are handled by Selenium's explicit wait rather than by
   * retrying the business action itself.
   */
  protected void click(By locator) {
    WebElement element =
        wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(locator)));
    ((JavascriptExecutor) driver)
        .executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", element);
    wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
  }

  protected void clickAndWaitForUrl(By locator, String urlFragment) {
    click(locator);
    wait.until(ExpectedConditions.urlContains(urlFragment));
    waitForDocumentReady();
  }

  /**
   * Focuses and replaces the value of an input, then waits until the browser
   * reports the requested value. No arbitrary sleep is used.
   */
  protected void sendKeys(By locator, String text) {
    WebElement element =
        wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(locator)));
    element.click();
    element.sendKeys(Keys.CONTROL, "a");
    element.sendKeys(Keys.BACK_SPACE);
    element.sendKeys(text);
    wait.until(ExpectedConditions.attributeToBe(locator, "value", text));
  }

  protected String getText(By locator) {
    waitForElement(locator);
    return driver.findElement(locator).getText();
  }

  protected boolean isDisplayed(By locator) {
    try {
      for (WebElement element : driver.findElements(locator)) {
        if (element.isDisplayed()) {
          return true;
        }
      }
      return false;
    } catch (WebDriverException e) {
      return false;
    }
  }

  protected boolean isElementPresent(By locator) {
    return !driver.findElements(locator).isEmpty();
  }

  public void logout() {
    clickAndWaitForUrl(
        By.cssSelector("form[action='/logout'] button[type='submit']"), "login");
  }

  private void waitForDocumentReady() {
    wait.until(
        d -> {
          try {
            return "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState"));
          } catch (WebDriverException e) {
            return false;
          }
        });
  }
}
