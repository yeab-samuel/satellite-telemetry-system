package com.aau.satellite.system;

import java.time.Duration;
import java.util.Arrays;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Base class for all Selenium system tests.
 *
 * <p>Chrome always runs headless here: no browser window is opened and no tab is shown on
 * screen. The suite runs entirely in the background. Because nothing is visible while it runs,
 * {@link SystemTestReporter} prints a PASS/FAIL line per test and saves a screenshot on failure
 * so the results are still easy to inspect afterwards.
 */
@ExtendWith(SystemTestReporter.class)
abstract class BaseTest {

  protected WebDriver driver;
  protected WebDriverWait wait;
  protected static final String BASE_URL = "http://localhost:8080";

  public enum UserRole {
    ADMIN("admin", "admin123"),
    OPERATOR("operator", "operator123"),
    ENGINEER("engineer", "engineer123"),
    VIEWER("viewer", "viewer123");

    private final String username;
    private final String password;

    UserRole(String username, String password) {
      this.username = username;
      this.password = password;
    }

    public String getUsername() {
      return username;
    }

    public String getPassword() {
      return password;
    }

    static UserRole fromUsername(String username) {
      return Arrays.stream(values())
          .filter(role -> role.username.equals(username))
          .findFirst()
          .orElse(UserRole.OPERATOR);
    }
  }

  @BeforeEach
  void setup() {
    ChromeOptions options = new ChromeOptions();
    // Headless: runs Chrome in the background, no window/tab is ever shown.
    options.addArguments(
        "--headless=new",
        "--no-sandbox",
        "--disable-dev-shm-usage",
        "--disable-gpu",
        "--window-size=1920,1080");
    driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    wait = new WebDriverWait(driver, Duration.ofSeconds(15));
  }

  @AfterEach
  void teardown() {
    if (driver != null) {
      driver.quit();
    }
  }

  protected DashboardPage loginAs(String username, String password) {
    driver.get(BASE_URL + "/login");
    driver.findElement(By.id("username")).sendKeys(username);
    driver.findElement(By.id("password")).sendKeys(password);
    driver.findElement(By.cssSelector("button.btn")).click();
    wait.until(ExpectedConditions.urlContains("dashboard"));
    return new DashboardPage(driver, UserRole.fromUsername(username));
  }

  protected DashboardPage loginAsRole(UserRole role) {
    return loginAs(role.getUsername(), role.getPassword());
  }

  protected void navigateTo(String path) {
    driver.get(BASE_URL + path);
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("main")));
  }
}
