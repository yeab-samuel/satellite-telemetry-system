package com.aau.satellite.system;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
 * <p>The test harness deliberately uses explicit synchronization only. A single
 * implicit wait mixed with explicit waits makes Selenium timing dependent on the
 * order in which elements are queried, so implicit waits are disabled here.
 */
@ExtendWith(SystemTestReporter.class)
abstract class BaseTest {

  protected WebDriver driver;
  protected WebDriverWait wait;
  protected static final String BASE_URL = "http://localhost:8080";
  private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(30);
  private static final Duration SERVER_TIMEOUT = Duration.ofSeconds(60);

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
    waitForServer();

    ChromeOptions options = new ChromeOptions();
    options.addArguments(
        "--headless=new",
        "--no-sandbox",
        "--disable-dev-shm-usage",
        "--disable-gpu",
        "--window-size=1920,1080",
        "--incognito",
        "--disable-extensions",
        "--disable-background-networking",
        "--disable-sync");

    driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(Duration.ZERO);
    driver.manage().timeouts().pageLoadTimeout(WAIT_TIMEOUT);
    driver.manage().timeouts().scriptTimeout(WAIT_TIMEOUT);
    driver.manage().window().setSize(new Dimension(1920, 1080));
    wait = new WebDriverWait(driver, WAIT_TIMEOUT);
  }

  @AfterEach
  void teardown() {
    if (driver != null) {
      try {
        driver.manage().deleteAllCookies();
      } finally {
        driver.quit();
      }
    }
  }

  protected DashboardPage loginAs(String username, String password) {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    loginPage.loginAs(username, password);
    wait.until(ExpectedConditions.urlContains("/dashboard"));
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("main")));
    return new DashboardPage(driver, UserRole.fromUsername(username));
  }

  protected DashboardPage loginAsRole(UserRole role) {
    return loginAs(role.getUsername(), role.getPassword());
  }

  protected void navigateTo(String path) {
    driver.get(BASE_URL + path);
    waitForDocumentReady();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("main")));
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

  private void waitForServer() {
    HttpClient client =
        HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/login"))
            .timeout(Duration.ofSeconds(5))
            .GET()
            .build();

    long deadline = System.nanoTime() + SERVER_TIMEOUT.toNanos();
    while (System.nanoTime() < deadline) {
      try {
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        if (response.statusCode() >= 200 && response.statusCode() < 500) {
          return;
        }
      } catch (Exception ignored) {
        // The Spring Boot process may still be starting. Poll until it is ready.
      }
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Interrupted while waiting for the application", e);
      }
    }

    throw new IllegalStateException(
        "Application did not become ready at " + BASE_URL + " within " + SERVER_TIMEOUT);
  }
}
