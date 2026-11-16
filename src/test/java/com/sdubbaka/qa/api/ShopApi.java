package com.sdubbaka.qa.api;

import static io.restassured.RestAssured.given;

import com.sdubbaka.qa.config.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import java.util.Map;

/** REST Assured client for the demo shop. Logs each request URI; full request/response logged when validation fails. */
public class ShopApi {
    private final RequestSpecification spec;
    private String token;

    public ShopApi() {
        this.spec = new RequestSpecBuilder()
                .setBaseUri(Config.baseUrl())
                .setContentType(ContentType.JSON)
                .log(LogDetail.URI)
                .build();
        io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private RequestSpecification req() {
        RequestSpecification r = given().spec(spec);
        return token == null ? r : r.header("Authorization", "Bearer " + token);
    }

    public Response login(String username, String password) {
        Response res = given().spec(spec).body(Map.of("username", username, "password", password)).post("/api/login");
        if (res.statusCode() == 200) token = res.jsonPath().getString("token");
        return res;
    }

    public ShopApi withToken(String t) {
        this.token = t;
        return this;
    }

    public Response products() { return req().get("/api/products"); }
    public Response product(int id) { return req().get("/api/products/" + id); }
    public Response orders() { return req().get("/api/orders"); }

    public Response createOrder(List<Map<String, Integer>> items) {
        return req().body(Map.of("items", items)).post("/api/orders");
    }

    public static Map<String, Integer> item(int productId, int quantity) {
        return Map.of("productId", productId, "quantity", quantity);
    }
}
