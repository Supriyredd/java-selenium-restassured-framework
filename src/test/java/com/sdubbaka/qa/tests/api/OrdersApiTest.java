package com.sdubbaka.qa.tests.api;

import static com.sdubbaka.qa.api.ShopApi.item;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;

import com.sdubbaka.qa.api.ShopApi;
import com.sdubbaka.qa.config.Config;
import java.util.List;
import java.util.Map;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class OrdersApiTest {

    private ShopApi loggedIn() {
        ShopApi api = new ShopApi();
        api.login(Config.username(), Config.password()).then().statusCode(200);
        return api;
    }

    @Test(groups = "smoke")
    public void loginReturnsToken() {
        new ShopApi().login(Config.username(), Config.password()).then()
                .statusCode(200)
                .body("token", matchesPattern("^[a-f0-9]{32}$"));
    }

    @Test
    public void lockedUserIsForbidden() {
        new ShopApi().login("locked_user", Config.password()).then().statusCode(403);
    }

    @Test
    public void ordersRequireToken() {
        new ShopApi().orders().then().statusCode(401);
    }

    @Test(groups = "smoke")
    public void createOrderIsSchemaValidAndTotalled() {
        loggedIn().createOrder(List.of(item(1, 3))).then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/order.schema.json"))
                .body("total", equalTo(74.97f));
    }

    @DataProvider(name = "invalidOrders")
    public Object[][] invalidOrders() {
        return new Object[][] {
            {"empty items", List.<Map<String, Integer>>of(), 400},
            {"unknown product", List.of(item(999, 1)), 422},
            {"zero quantity", List.of(item(1, 0)), 422},
            {"out of stock", List.of(item(3, 1)), 409},
        };
    }

    @Test(dataProvider = "invalidOrders")
    public void invalidOrdersAreRejected(String name, List<Map<String, Integer>> items, int status) {
        loggedIn().createOrder(items).then().statusCode(status).body("error", notNullValue());
    }
}
