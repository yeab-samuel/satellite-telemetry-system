package com.aau.satellite.system;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Selenium now drives Chrome headlessly (see BaseTest) so there is no visible window to watch
 * while the suite runs. This extension is how the results are still surfaced:
 *
 * <ul>
 *   <li>Every system test prints a clear PASS/FAIL line to the console as it finishes.
 *   <li>On failure, a full-page screenshot of the browser at the moment of failure is written to
 *       {@code target/selenium-screenshots/}, named after the test, so a human can see exactly
 *       what the headless browser was looking at.
 * </ul>
 *
 * Both Maven (surefire/failsafe reports under {@code target/surefire-reports} and {@code
 * target/failsafe-reports}) and this extension's console output and screenshots together make up
 * the "results" of the headless run.
 */
public class SystemTestReporter implements AfterTestExecutionCallback {

  private static final Path SCREENSHOT_DIR = Paths.get("target", "selenium-screenshots");
  private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("HHmmss");

  @Override
  public void afterTestExecution(ExtensionContext context) {
    String testId = context.getRequiredTestClass().getSimpleName()
        + "."
        + context.getRequiredTestMethod().getName();

    if (context.getExecutionException().isPresent()) {
      System.out.println("[SYSTEM TEST] FAIL - " + testId
          + " -> " + context.getExecutionException().get().getMessage());
      saveScreenshot(context, testId);
    } else {
      System.out.println("[SYSTEM TEST] PASS - " + testId);
    }
  }

  private void saveScreenshot(ExtensionContext context, String testId) {
    Object instance = context.getRequiredTestInstance();
    if (!(instance instanceof BaseTest baseTest) || baseTest.driver == null) {
      return;
    }
    WebDriver driver = baseTest.driver;
    if (!(driver instanceof TakesScreenshot shot)) {
      return;
    }
    try {
      Files.createDirectories(SCREENSHOT_DIR);
      File src = shot.getScreenshotAs(OutputType.FILE);
      String fileName = testId + "_" + java.time.LocalTime.now().format(TIMESTAMP) + ".png";
      Path dest = SCREENSHOT_DIR.resolve(fileName);
      Files.copy(src.toPath(), dest);
      System.out.println("[SYSTEM TEST] Screenshot saved: " + dest);
    } catch (IOException e) {
      System.out.println("[SYSTEM TEST] Could not save screenshot for " + testId + ": " + e.getMessage());
    }
  }
}
