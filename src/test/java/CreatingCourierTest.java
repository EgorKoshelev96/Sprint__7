import com.github.javafaker.Faker;
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
    private Faker faker = new Faker();
    private String validLogin;
    private String validFirstName;
    private String validPassword;

    String courierId;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        validLogin = faker.name().username();
        validFirstName = faker.name().firstName();
        validPassword = faker.internet().password(6, 10, true, true, true);
    }

    @AfterEach
    public void deleteCourier() {
        CourierLoginInSystem сourierLoginInSystem = new CourierLoginInSystem(validLogin, validPassword);
        Response loginResponse =
                given().header("Content-Type", "application/json").and()
                        .body(сourierLoginInSystem).when().post("/api/v1/courier/login");

        if (loginResponse.statusCode() == 200) {
            courierId = loginResponse.jsonPath().getString("id");
            if (courierId != null) {
                given().pathParam("id", courierId)
                        .when().delete("/api/v1/courier/{id}")
                        .then().statusCode(200);
        }
    }
    }

    @Test
    @DisplayName("Courier can be created")
    @Description("Courier can be created for /api/v1/courier")
    public void creatingCourier() {
        CreatingCourier creatingCourier = new CreatingCourier(validLogin, validPassword, validFirstName);
        Response response =
                given().header("Content-Type", "application/json")
                        .and().body(creatingCourier).when().post("/api/v1/courier");
        System.out.println(response.body().asString());
        response.then().assertThat().body("ok", equalTo(true)).and()
                .statusCode(201);

    }


    @Test
    @DisplayName("You can't create two identical couriers")
    @Description("You can't create two identical couriers for /api/v1/courier/login")
    public void cantCreateIdenticalCouriers() {
        CreatingCourier creatingIdenticalCourier = new CreatingCourier(validLogin, validPassword, validFirstName);
        Response response =
                given().header("Content-Type", "application/json")
                        .and().body(creatingIdenticalCourier ).when().post("/api/v1/courier");
        response.then().assertThat().body("ok", equalTo(true)).and()
                .statusCode(201);
        Response responseTwo =
                given().header("Content-Type", "application/json")
                .and().body(creatingIdenticalCourier ).when().post("/api/v1/courier");
        responseTwo.then().statusCode(409).body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
        System.out.println(responseTwo.body().asString());
    }

    @Test
    @DisplayName("If one of the fields is missing, the query returns an Login error")
    @Description("If one of the fields is missing, the query returns an Login error for /api/v1/courier")
    public void fieldsMissingQueryReturnsLoginError() {
        CreatingCourier creatingMissingLoginCourier = new CreatingCourier(null, validPassword, validFirstName);
        Response responseLogin =
                given().header("Content-Type", "application/json")
                        .and().body(creatingMissingLoginCourier).when().post("/api/v1/courier");
        responseLogin.then().statusCode(400).body("message", equalTo("Недостаточно данных для создания учетной записи"));
        System.out.println(responseLogin.body().asString());

    }

    @Test
    @DisplayName("If one of the fields is missing, the query returns an Password error")
    @Description("If one of the fields is missing, the query returns an Password error for /api/v1/courier")
    public void fieldsMissingQueryReturnsPasswordError() {
        CreatingCourier creatingMissingPasswordCourier = new CreatingCourier(validLogin, null, validFirstName);
        Response responsePassword =
                given().header("Content-Type", "application/json")
                        .and().body(creatingMissingPasswordCourier ).when().post("/api/v1/courier");
        responsePassword.then().statusCode(400).body("message", equalTo("Недостаточно данных для создания учетной записи"));
        System.out.println(responsePassword.body().asString());

    }

    @Test
    @DisplayName("If one of the fields is missing, the query returns an Password error")
    @Description("If one of the fields is missing, the query returns an Password error for /api/v1/courier")
    public void fieldsMissingQueryReturnsFirstNameError() {
        CreatingCourier creatingMissingFirstNameCourier = new CreatingCourier(validLogin, validPassword, null);
        Response responseFirstName=
                given().header("Content-Type", "application/json")
                        .and().body(creatingMissingFirstNameCourier).when().post("/api/v1/courier");
        responseFirstName.then().statusCode(400).body("message", equalTo("Недостаточно данных для создания учетной записи"));
        System.out.println(responseFirstName.body().asString());

    }
    @Test
    @DisplayName("If you create a user with a login that already exists, an error will be returned")
    @Description("If you create a user with a login that already exists, an error will be returned for /api/v1/courier")
    public void createUserWithSameLoginError() {
        CreatingCourier creatingUserWithSameLoginCourier = new CreatingCourier(validLogin, validPassword, validFirstName);
        Response response =
                given().header("Content-Type", "application/json")
                        .and().body(creatingUserWithSameLoginCourier ).when().post("/api/v1/courier");
        response.then().assertThat().body("ok", equalTo(true)).and()
                .statusCode(201);

        Response responseSameLogin=
                given().header("Content-Type", "application/json")
                        .and().body(creatingUserWithSameLoginCourier).when().post("/api/v1/courier");
        responseSameLogin.then().statusCode(409).body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
        System.out.println(responseSameLogin.body().asString());

    }

}

