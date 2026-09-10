package com.aau.satellite.system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

@Tag("system")
@DisplayName("Navigation Tests")
class NavigationTests extends BaseTest {

  @Test
  @DisplayName("Topbar navigation works for all pages")
  void topbarNavigation_worksForAllPages() {
    loginAs("operator", "operator123");

    String[] pages = {"satellites", "missions"};

    for (String page : pages) {
      WebElement navLink = driver.findElement(By.cssSelector("a[href*='" + page + "']"));
      navLink.click();
      wait.until(ExpectedConditions.urlContains(page));
      assertTrue(driver.getCurrentUrl().contains(page));
    }
  }

  @Test
  @DisplayName("Error page displays user-friendly message")
  void errorPage_displaysUserFriendlyMessage() {
    driver.get(BASE_URL + "/error");

    WebElement errorContent =
        driver.findElement(By.cssSelector(".error-page, .error-container, main"));
    assertTrue(errorContent.isDisplayed());
  }

  @Test
  @DisplayName("Invalid URL shows error page")
  void invalidUrl_redirectsToErrorPage() {
    driver.get(BASE_URL + "/invalid-page-that-does-not-exist");

    String currentUrl = driver.getCurrentUrl();
    assertTrue(currentUrl.contains("error") || currentUrl.contains("404"));
  }
}
