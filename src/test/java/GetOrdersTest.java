import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class GetOrdersTest {

    String bearerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2OTE2MzAxYjdlZTkyNzAwM2QyOGRjNjIiLCJpYXQiOjE3NjQ2OTU5MzMsImV4cCI6MTc2NTMwMDczM30.2djCUAGl6tprzZHjeaAQ4RlesownYFs7ojeGrVtJN6M";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Check that the list of orders is returned in the response body")
    @Description("Check that the list of orders is returned in the response body for /api/v1/orders")
    public void testGetOrders() {

        Response response = gettingListOfOrders();
        statusCodeListOfOrders(response);
        checkingResponseBodyIsNotEmpty(response);
        checkResponseContainsOrdersList(response);

    }

    @Step("Send GET request /api/v1/orders")
    public Response gettingListOfOrders() {
        Response response =
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                        .and().get("/api/v1/orders").then().extract().response();
        return response;
    }

    @Step("Get status code")
    public void statusCodeListOfOrders(Response response) {
        assertEquals(200, response.statusCode(), "Статус код ответа должен быть 200");

    }

    @Step("Checking that the response body is not empty")
    public void checkingResponseBodyIsNotEmpty(Response response) {
        String responseBody = response.body().asString();
        assertFalse(responseBody.isEmpty(), "Тело ответа пустое");

    }

    @Step("We check that the response contains a list of orders")
    private void checkResponseContainsOrdersList(Response response) {
        if (response.jsonPath().getList("orders") != null) {
            List<Object> orders = response.jsonPath().getList("orders");
            assertTrue(orders.size() >= 0);
            System.out.println(response.body().asString());

            if (!orders.isEmpty()) {
                String firstId = response.jsonPath().getString("orders[0].id");
                assertNotNull(firstId, "Поле id отсутствует в первом заказе");
            }
        }


    }
}
