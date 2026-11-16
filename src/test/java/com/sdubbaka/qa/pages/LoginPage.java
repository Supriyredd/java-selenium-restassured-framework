package com.sdubbaka.qa.pages;

import com.sdubbaka.qa.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {
    private static final By USERNAME = testId("username");
    private static final By PASSWORD = testId("password");
    private static final By SUBMIT = testId("login-button");
    private static final By ERROR = testId("error");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(Config.baseUrl() + "/");
        visible(USERNAME);
        return this;
    }

    public InventoryPage loginAs(String user, String password) {
        submit(user, password);
        return new InventoryPage(driver).waitUntilLoaded();
    }

    public LoginPage submit(String user, String password) {
        type(USERNAME, user);
        type(PASSWORD, password);
        click(SUBMIT);
        return this;
    }

    public String errorText() {
        return visible(ERROR).getText();
    }
}
