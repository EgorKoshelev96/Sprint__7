import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginInSystemTest {
    private Faker faker = new Faker();
    private String validLogin;
    private String validPassword;
    private String validFirstName;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        validLogin = faker.name().username();
        validPassword = faker.internet().password(6, 10, true, true, true);
    }

    @Test
    @DisplayName("The courier can log in")
    @Description("The courier can log in for /api/v1/courier/login")
    public void courierAuthorizationAndReturnId() {
        CreatingCourier creatingCourier = new CreatingCourier(validLogin, validPassword, validFirstName);
        CourierLoginInSystem сourierLoginInSystem = new CourierLoginInSystem(validLogin, validPassword);
                given().header("Content-Type", "application/json")
                        .and().body(creatingCourier).when().post("/api/v1/courier");
        Response response =
                given().header("Content-Type", "application/json")
                        .and().body(сourierLoginInSystem).when().post("/api/v1/courier/login");
        response.then().assertThat().body("id", notNullValue()).and()
                .statusCode(200);
    }

    @Test
    @DisplayName("To authorize, you must submit all required fields Login")
    @Description("To authorize, you must submit all required fields Login for /api/v1/courier/login")
    public void passingRequiredFieldsLogin() {
        CourierLoginInSystem withoutLogin = new CourierLoginInSystem(null, validPassword);
        Response responseLogin =
                given().header("Content-Type", "application/json")
                        .and().body(withoutLogin).when().post("/api/v1/courier/login");
        responseLogin.then().statusCode(400).body("message", equalTo("Недостаточно данных для входа"));
        System.out.println(responseLogin.body().asString());
    }

    @Test
    @DisplayName("To authorize, you must submit all required fields Password")
    @Description("To authorize, you must submit all required fields Password for /api/v1/courier/login")
    public void passingRequiredFieldsPassword() {
        CourierLoginInSystem withoutPassword = new CourierLoginInSystem(validLogin, null);
        Response responsePassword =
                given().header("Content-Type", "application/json")
                        .and().body(withoutPassword).when().post("/api/v1/courier/login");
        responsePassword.then().statusCode(400).body("message", equalTo("Недостаточно данных для входа"));
        System.out.println(responsePassword.body().asString());
    }

    @Test
    @DisplayName("The system will return an error if you enter an incorrect login")
    @Description("The system will return an error if you enter an incorrect login for /api/v1/courier/login")
    public void systemReturnErrorIfIncorrectLogin() {
        String incorrectLogin = validLogin + "_invalid";
        CourierLoginInSystem incorrect = new CourierLoginInSystem(incorrectLogin, validPassword);
        Response responseLogin =
                given().header("Content-Type", "application/json")
                        .and().body(incorrect).when().post("/api/v1/courier/login");
        responseLogin.then().statusCode(404).body("message", equalTo("Учетная запись не найдена"));
        System.out.println(responseLogin.body().asString());
    }

    @Test
    @DisplayName("The system will return an error if you enter an incorrect Password")
    @Description("The system will return an error if you enter an incorrect Password for /api/v1/courier/login")
    public void systemReturnErrorIfIncorrectPassword() {
        String incorrectPassword = faker.internet().password(6, 10, true, true, true);
        CourierLoginInSystem incorrect = new CourierLoginInSystem(validLogin, incorrectPassword);
        Response responsePassword =
                given().header("Content-Type", "application/json")
                        .and().body(incorrect).when().post("/api/v1/courier/login");
        responsePassword.then().statusCode(404).body("message", equalTo("Учетная запись не найдена"));
        System.out.println(responsePassword.body().asString());
    }

    @Test
    @DisplayName("If you log in using a non-existent user, the request returns an error")
    @Description("If you log in using a non-existent user, the request returns an error for /api/v1/courier/login")
    public void authorizationUnderNonExistentUserError() {
        String randomLogin = faker.name().username();
        String randomPassword = faker.internet().password(6, 10, true, true, true);
        CourierLoginInSystem nonExistentUser = new CourierLoginInSystem(randomLogin, randomPassword);
        Response responseNonExistentUser =
                given().header("Content-Type", "application/json")
                        .and().body(nonExistentUser).when().post("/api/v1/courier/login");
        responseNonExistentUser.then().statusCode(404).body("message", equalTo("Учетная запись не найдена"));
        System.out.println(responseNonExistentUser.body().asString());
    }


}
