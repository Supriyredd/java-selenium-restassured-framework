package com.sdubbaka.qa.tests.ui;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import com.sdubbaka.qa.api.ShopApi;
import com.sdubbaka.qa.config.Config;
import com.sdubbaka.qa.pages.CartPage;
import com.sdubbaka.qa.pages.InventoryPage;
import com.sdubbaka.qa.pages.LoginPage;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class CheckoutTest extends BaseUiTest {

    private InventoryPage login() {
        return new LoginPage(driver()).open().loginAs(Config.username(), Config.password());
    }

    @Test(groups = "regression")
    public void outOfStockProductCannotBeAdded() {
        assertFalse(login().addButton(3).isEnabled());
    }

    @Test(groups = "regression")
    public void cartCountAddsQuantities() {
        assertEquals(login().add(1, 2).add(2, 1).cartCount(), "3");
    }

    /** UI order is checked again through the API with the browser's own session token. */
    @Test(groups = {"smoke", "regression"})
    public void uiOrderMatchesApiOrder() {
        InventoryPage inventory = login().add(1, 2).add(4, 1);
        String token = inventory.sessionToken();
        CartPage cart = inventory.openCart();
        assertEquals(cart.itemCount(), 2);
        assertEquals(cart.total(), "$95.23");

        int orderId = cart.placeOrder();
        assertEquals(cart.confirmationText(), "Order #" + orderId + " confirmed");

        Response orders = new ShopApi().withToken(token).orders();
        assertEquals(orders.statusCode(), 200);
        Number total = orders.jsonPath().get("find { it.id == " + orderId + " }.total");
        assertEquals(total.doubleValue(), 95.23, 0.001);
    }
}
