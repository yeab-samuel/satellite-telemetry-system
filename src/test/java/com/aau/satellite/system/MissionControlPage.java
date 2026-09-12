package com.aau.satellite.system;

import org.openqa.selenium.*;

public class MissionControlPage {
  final WebDriver driver;

  public MissionControlPage(WebDriver d) {
    driver = d;
  }

  public void open(String url) {
    driver.get(url);
  }

  public void login(String u, String p) {
    driver.findElement(By.name("username")).sendKeys(u);
    driver.findElement(By.name("password")).sendKeys(p);
    driver.findElement(By.cssSelector("button.btn")).click();
  }

  public boolean dashboardVisible() {
    return driver.getTitle().contains("Mission Control")
        && driver.getPageSource().contains("Mission Operations Dashboard");
  }
}
