import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest {

    String bearerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2OTE2MzAxYjdlZTkyNzAwM2QyOGRjNjIiLCJpYXQiOjE3NjQ2OTU5MzMsImV4cCI6MTc2NTMwMDczM30.2djCUAGl6tprzZHjeaAQ4RlesownYFs7ojeGrVtJN6M";

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @ParameterizedTest
    @CsvSource({
            "Альберт,Рубцов,Проспект Мира д.3,Сокол,+7 800 355 35 35,2,2025-12-06,Оставить у двери,'BLACK,GREY'",
            "Альберт,Рубцов,Проспект Мира д.3,Сокол,+7 800 355 35 35,2,2025-12-06,Оставить у двери,GREY",
            "Альберт,Рубцов,Проспект Мира д.3,Сокол,+7 800 355 35 35,2,2025-12-06,Оставить у двери,BLACK",
            "Альберт,Рубцов,Проспект Мира д.3,Сокол,+7 800 355 35 35,2,2025-12-06,Оставить у двери,null",
    })
    void testCreateOrder(String firstName, String lastName, String address, String metroStation, String phone,
                   Integer rentTime, String deliveryDate, String comment, String colorStr) {
        List<String> colors = Arrays.asList(colorStr.split(","));

        CreateOrder order = new CreateOrder();
        order.setFirstName(firstName);
        order.setLastName(lastName);
        order.setAddress(address);
        order.setMetroStation(metroStation);
        order.setPhone(phone);
        order.setRentTime(rentTime);
        order.setDeliveryDate(deliveryDate);
        order.setComment(comment);
        order.setColor(colors);

        Response response = given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                .and().body(order).when().post("/api/v1/orders");
        response.then().assertThat().body("track", notNullValue()).and()
                .statusCode(201);
        int trackId = response.jsonPath().getInt("track");
        System.out.println("Заказ создан.ID: " + trackId);
    }

}
