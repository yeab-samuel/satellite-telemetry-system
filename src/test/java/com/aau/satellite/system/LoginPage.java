package com.aau.satellite.system;

import com.aau.satellite.system.BaseTest.UserRole;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

  // Locators
  private final By usernameField = By.id("username");
  private final By passwordField = By.id("password");
  private final By submitButton = By.cssSelector("button.btn");
  private final By errorMessage = By.cssSelector(".notice.error");
  private final By successMessage = By.cssSelector(".notice.success");
  private final By brandMark = By.cssSelector(".brand-mark");
  private final By loginShell = By.cssSelector(".login-shell");
  private final By demoAccounts = By.cssSelector(".demo-account");

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public void open() {
    navigateTo(BASE_URL + "/login");
    waitForElement(loginShell);
  }

  public void enterUsername(String username) {
    sendKeys(usernameField, username);
  }

  public void enterPassword(String password) {
    sendKeys(passwordField, password);
  }

  public void clickSubmit() {
    click(submitButton);
  }

  public void loginAs(String username, String password) {
    enterUsername(username);
    enterPassword(password);
    clickSubmit();
  }

  public DashboardPage loginAsRole(UserRole role) {
    loginAs(role.getUsername(), role.getPassword());
    wait.until(ExpectedConditions.urlContains("dashboard"));
    return new DashboardPage(driver, role);
  }

  public DashboardPage loginAsAdmin() {
    return loginAsRole(UserRole.ADMIN);
  }

  public DashboardPage loginAsOperator() {
    return loginAsRole(UserRole.OPERATOR);
  }

  public DashboardPage loginAsEngineer() {
    return loginAsRole(UserRole.ENGINEER);
  }

  public DashboardPage loginAsViewer() {
    return loginAsRole(UserRole.VIEWER);
  }

  public boolean isLoginPageLoaded() {
    return isDisplayed(loginShell) && isDisplayed(brandMark);
  }

  public boolean isErrorDisplayed() {
    return isDisplayed(errorMessage);
  }

  public String getErrorMessage() {
    return getText(errorMessage);
  }

  public boolean isSuccessMessageDisplayed() {
    return isDisplayed(successMessage);
  }

  public String getSuccessMessage() {
    return getText(successMessage);
  }

  public int getDemoAccountsCount() {
    return driver.findElements(demoAccounts).size();
  }

  public boolean isUsernameFieldVisible() {
    return isDisplayed(usernameField);
  }

  public boolean isPasswordFieldVisible() {
    return isDisplayed(passwordField);
  }

  public boolean isSubmitButtonVisible() {
    return isDisplayed(submitButton);
  }
}
