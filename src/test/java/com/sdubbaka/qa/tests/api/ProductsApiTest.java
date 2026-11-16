package com.sdubbaka.qa.tests.api;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import com.sdubbaka.qa.api.ShopApi;
import java.util.HashSet;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductsApiTest {
    private final ShopApi api = new ShopApi();

    @Test(groups = "smoke")
    public void productsMatchSchema() {
        api.products().then()
                .statusCode(200)
                .contentType("application/json")
                .body("size()", greaterThan(0))
                .body(matchesJsonSchemaInClasspath("schemas/products.schema.json"));
    }

    @Test
    public void productIdsAreUnique() {
        List<Integer> ids = api.products().jsonPath().getList("id", Integer.class);
        Assert.assertEquals(new HashSet<>(ids).size(), ids.size());
    }

    @Test
    public void unknownProductReturns404() {
        api.product(9999).then().statusCode(404).body("error", equalTo("Product not found"));
    }
}
