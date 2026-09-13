package com.aau.satellite.system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

@Tag("system")
@DisplayName("Login Functionality Tests")
class LoginSystemTest extends BaseTest {

  @Test
  @DisplayName("Login page loads with all required elements")
  void loginPage_loadsCorrectly() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();

    assertTrue(loginPage.isLoginPageLoaded());
    assertTrue(loginPage.isUsernameFieldVisible());
    assertTrue(loginPage.isPasswordFieldVisible());
    assertTrue(loginPage.isSubmitButtonVisible());
    assertEquals(4, loginPage.getDemoAccountsCount());
  }

  @Test
  @DisplayName("Operator can login successfully")
  void login_withOperatorCredentials_redirectsToDashboard() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();

    DashboardPage dashboard = loginPage.loginAsOperator();

    assertTrue(dashboard.isDashboardLoaded());
    assertTrue(dashboard.isStatsGridDisplayed());
  }

  @Test
  @DisplayName("Admin can login successfully")
  void login_withAdminCredentials_redirectsToDashboard() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();

    DashboardPage dashboard = loginPage.loginAsAdmin();

    assertTrue(dashboard.isDashboardLoaded());
  }

  @Test
  @DisplayName("Engineer can login successfully")
  void login_withEngineerCredentials_redirectsToDashboard() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();

    DashboardPage dashboard = loginPage.loginAsEngineer();

    assertTrue(dashboard.isDashboardLoaded());
  }

  @Test
  @DisplayName("Viewer can login successfully")
  void login_withViewerCredentials_redirectsToDashboard() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();

    DashboardPage dashboard = loginPage.loginAsViewer();

    assertTrue(dashboard.isDashboardLoaded());
  }

  @Test
  @DisplayName("Invalid credentials show error message")
  void login_withInvalidCredentials_showsError() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();

    loginPage.loginAs("wronguser", "wrongpass");

    assertTrue(loginPage.isErrorDisplayed());
    assertTrue(loginPage.getErrorMessage().contains("Username or password is incorrect"));
  }

  @Test
  @DisplayName("Logout redirects to login page")
  void logout_redirectsToLogin() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.open();
    DashboardPage dashboard = loginPage.loginAsOperator();

    dashboard.logout();

    assertTrue(loginPage.isLoginPageLoaded());
    assertTrue(loginPage.isSuccessMessageDisplayed());
  }
}
