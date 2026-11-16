package com.sdubbaka.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class InventoryPage extends BasePage {
    private static final By PRODUCT = testId("product");
    private static final By CART_COUNT = testId("cart-count");
    private static final By CART_LINK = testId("cart-link");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public InventoryPage waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains("inventory.html"));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(PRODUCT, 0));
        return this;
    }

    public int productCount() {
        return driver.findElements(PRODUCT).size();
    }

    public WebElement addButton(int productId) {
        return visible(testId("add-" + productId));
    }

    public InventoryPage add(int productId, int times) {
        for (int i = 0; i < times; i++) click(testId("add-" + productId));
        return this;
    }

    public String cartCount() {
        return visible(CART_COUNT).getText();
    }

    public CartPage openCart() {
        click(CART_LINK);
        return new CartPage(driver);
    }

    /** Session token stored by the app; used for cross-layer API checks. */
    public String sessionToken() {
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return sessionStorage.getItem('token');");
    }
}
