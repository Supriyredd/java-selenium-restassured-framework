package com.sdubbaka.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage extends BasePage {
    private static final By ITEM = testId("cart-item");
    private static final By TOTAL = testId("cart-total");
    private static final By CHECKOUT = testId("checkout");
    private static final By CONFIRMATION = testId("order-confirmation");

    public CartPage(WebDriver driver) {
        super(driver);
        visible(TOTAL);
    }

    public int itemCount() {
        return driver.findElements(ITEM).size();
    }

    public String total() {
        return visible(TOTAL).getText();
    }

    /** Places the order and returns the order id shown in the confirmation. */
    public int placeOrder() {
        click(CHECKOUT);
        WebElement c = visible(CONFIRMATION);
        return Integer.parseInt(c.getAttribute("data-order-id"));
    }

    public String confirmationText() {
        return visible(CONFIRMATION).getText();
    }
}
