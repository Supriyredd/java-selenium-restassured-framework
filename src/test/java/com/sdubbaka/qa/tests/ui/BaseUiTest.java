package com.sdubbaka.qa.tests.ui;

import com.sdubbaka.qa.driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseUiTest {
    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        DriverFactory.get();
    }

    // Runs after the listener has taken any failure screenshot.
    @AfterMethod(alwaysRun = true)
    public void stopBrowser() {
        DriverFactory.quit();
    }

    public WebDriver driver() {
        return DriverFactory.get();
    }

    /** Used by the screenshot listener; returns null after the browser is closed. */
    public WebDriver currentDriver() {
        return DriverFactory.current();
    }
}
