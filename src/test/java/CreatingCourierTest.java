import io.restassured.RestAssured;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreatingCourierTest {
    CreatingCourier creatingCourier = new CreatingCourier("Egor5", "12345", "Egor_Koshelev");
    CourierLoginInSystem сourierLoginInSystem = new CourierLoginInSystem("Egor5", "12345");
    String courierId;
    String bearerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2OTE2MzAxYjdlZTkyNzAwM2QyOGRjNjIiLCJpYXQiOjE3NjQ2OTU5MzMsImV4cCI6MTc2NTMwMDczM30.2djCUAGl6tprzZHjeaAQ4RlesownYFs7ojeGrVtJN6M";

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @AfterEach
    public void deleteCourier() {
        Response loginResponse =
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken).and()
                        .body(сourierLoginInSystem).when().post("/api/v1/courier/login");

        if (loginResponse.statusCode() == 200) {
            courierId = loginResponse.jsonPath().getString("id");
            if (courierId != null) {
                given().auth().oauth2(bearerToken).pathParam("id", courierId)
                        .when().delete("/api/v1/courier/{id}")
                        .then().statusCode(200);
        }
    }
    }

    @Test
    @DisplayName("Courier can be created")
    @Description("Courier can be created for /api/v1/courier")
    public void creatingCourier() {
        Response response =
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                        .and().body(creatingCourier).when().post("/api/v1/courier");
        response.then().assertThat().body("ok", equalTo(true)).and()
                .statusCode(201);
        System.out.println(response.body().asString());
    }


    @Test
    @DisplayName("You can't create two identical couriers")
    @Description("You can't create two identical couriers for /api/v1/courier/login")
    public void cantCreateIdenticalCouriers() {
        Response response =
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                        .and().body(creatingCourier).when().post("/api/v1/courier");
        response.then().assertThat().body("ok", equalTo(true)).and()
                .statusCode(201);
        Response responseTwo =
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                .and().body(creatingCourier).when().post("/api/v1/courier");
        responseTwo.then().statusCode(409).body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
        System.out.println(responseTwo.body().asString());
    }

    @Test
    @DisplayName("If one of the fields is missing, the query returns an Login error")
    @Description("If one of the fields is missing, the query returns an Login error for /api/v1/courier")
    public void fieldsMissingQueryReturnsLoginError() {
        CreatingCourier creatingLoginCourier = new CreatingCourier(null, "12345", "Egor_Koshelev");
        Response responseLogin =
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                        .and().body(creatingLoginCourier).when().post("/api/v1/courier");
        responseLogin.then().statusCode(400).body("message", equalTo("Недостаточно данных для создания учетной записи"));
        System.out.println(responseLogin.body().asString());

    }

    @Test
    @DisplayName("If one of the fields is missing, the query returns an Password error")
    @Description("If one of the fields is missing, the query returns an Password error for /api/v1/courier")
    public void fieldsMissingQueryReturnsPasswordError() {
        CreatingCourier creatingPasswordCourier = new CreatingCourier("Egor5", null, "Egor_Koshelev" );
        Response responsePassword =
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                        .and().body(creatingPasswordCourier).when().post("/api/v1/courier");
        responsePassword.then().statusCode(400).body("message", equalTo("Недостаточно данных для создания учетной записи"));
        System.out.println(responsePassword.body().asString());

    }

    @Test
    @DisplayName("If one of the fields is missing, the query returns an Password error")
    @Description("If one of the fields is missing, the query returns an Password error for /api/v1/courier")
    public void fieldsMissingQueryReturnsFirstNameError() {
        CreatingCourier creatingFirstNameCourier = new CreatingCourier("Egor5", "12345", null );
        Response responseFirstName=
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                        .and().body(creatingFirstNameCourier).when().post("/api/v1/courier");
        responseFirstName.then().statusCode(400).body("message", equalTo("Недостаточно данных для создания учетной записи"));
        System.out.println(responseFirstName.body().asString());

    }
    @Test
    @DisplayName("If you create a user with a login that already exists, an error will be returned")
    @Description("If you create a user with a login that already exists, an error will be returned for /api/v1/courier")
    public void createUserwithSameLoginError() {
        Response response =
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                        .and().body(creatingCourier).when().post("/api/v1/courier");
        response.then().assertThat().body("ok", equalTo(true)).and()
                .statusCode(201);

        CreatingCourier creatingSameLoginCourier = new CreatingCourier("Egor5", "12345687", "Egor" );
        Response responseSameLogin=
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                        .and().body(creatingSameLoginCourier).when().post("/api/v1/courier");
        responseSameLogin.then().statusCode(409).body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
        System.out.println(responseSameLogin.body().asString());

    }

}

