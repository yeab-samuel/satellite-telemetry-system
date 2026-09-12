package com.aau.satellite.system;

import org.openqa.selenium.*;

public class ErrorPage extends BasePage {

  // Locators
  private final By errorContainer = By.cssSelector(".error-container, .error-page");
  private final By errorMessage = By.cssSelector(".error-message, .message");

  public ErrorPage(WebDriver driver) {
    super(driver);
  }

  public void open() {
    navigateTo(BASE_URL + "/error");
    waitForElement(errorContainer);
  }

  public boolean isErrorPageLoaded() {
    return isDisplayed(errorContainer);
  }

  public String getErrorMessage() {
    return getText(errorMessage);
  }
}
