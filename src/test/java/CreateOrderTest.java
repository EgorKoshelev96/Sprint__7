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

        Response response = given().header("Content-Type", "application/json")
                .and().body(order).when().post("/api/v1/orders");
        response.then().assertThat().body("track", notNullValue()).and()
                .statusCode(201);
        int trackId = response.jsonPath().getInt("track");
        System.out.println("Заказ создан.ID: " + trackId);
    }

}
