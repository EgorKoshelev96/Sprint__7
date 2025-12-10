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
    CourierLoginInSystem сourierLoginInSystem = new CourierLoginInSystem("Egor12", "12345");
    String bearerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2OTE2MzAxYjdlZTkyNzAwM2QyOGRjNjIiLCJpYXQiOjE3NjQ2OTU5MzMsImV4cCI6MTc2NTMwMDczM30.2djCUAGl6tprzZHjeaAQ4RlesownYFs7ojeGrVtJN6M";

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("The courier can log in")
    @Description("The courier can log in for /api/v1/courier/login")
    public void courierAuthorizationAndReturnId() {
        Response response =
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                        .and().body(сourierLoginInSystem).when().post("/api/v1/courier/login");
        response.then().assertThat().body("id", notNullValue()).and()
                .statusCode(200);
    }

    @Test
    @DisplayName("To authorize, you must submit all required fields Login")
    @Description("To authorize, you must submit all required fields Login for /api/v1/courier/login")
    public void passingRequiredFieldsLogin() {
        CourierLoginInSystem withoutLogin = new CourierLoginInSystem(null, "12345");
        Response responseLogin =
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                        .and().body(withoutLogin).when().post("/api/v1/courier/login");
        responseLogin.then().statusCode(400).body("message", equalTo("Недостаточно данных для входа"));
        System.out.println(responseLogin.body().asString());
    }

    @Test
    @DisplayName("To authorize, you must submit all required fields Password")
    @Description("To authorize, you must submit all required fields Password for /api/v1/courier/login")
    public void passingRequiredFieldsPassword() {
        CourierLoginInSystem withoutPassword = new CourierLoginInSystem("Egor12", null);
        Response responsePassword =
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                        .and().body(withoutPassword).when().post("/api/v1/courier/login");
        responsePassword.then().statusCode(400).body("message", equalTo("Недостаточно данных для входа"));
        System.out.println(responsePassword.body().asString());
    }

    @Test
    @DisplayName("The system will return an error if you enter an incorrect login")
    @Description("The system will return an error if you enter an incorrect login for /api/v1/courier/login")
    public void systemReturnErrorIfIncorrectLogin() {
        CourierLoginInSystem incorrectLogin = new CourierLoginInSystem("Egor122", "12345");
        Response responseLogin =
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                        .and().body(incorrectLogin).when().post("/api/v1/courier/login");
        responseLogin.then().statusCode(404).body("message", equalTo("Учетная запись не найдена"));
        System.out.println(responseLogin.body().asString());
    }

    @Test
    @DisplayName("The system will return an error if you enter an incorrect Password")
    @Description("The system will return an error if you enter an incorrect Password for /api/v1/courier/login")
    public void systemReturnErrorIfIncorrectPassword() {
        CourierLoginInSystem incorrectPassword = new CourierLoginInSystem("Egor122", "12346");
        Response responsePassword =
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                        .and().body(incorrectPassword).when().post("/api/v1/courier/login");
        responsePassword.then().statusCode(404).body("message", equalTo("Учетная запись не найдена"));
        System.out.println(responsePassword.body().asString());
    }

    @Test
    @DisplayName("If you log in using a non-existent user, the request returns an error")
    @Description("If you log in using a non-existent user, the request returns an error for /api/v1/courier/login")
    public void authorizationUnderNonExistentUserError() {
        CourierLoginInSystem nonExistentUser = new CourierLoginInSystem("Jora_Nesushestvuyshiy", "da12346");
        Response responseNonExistentUser =
                given().header("Content-Type", "application/json").auth().oauth2(bearerToken)
                        .and().body(nonExistentUser).when().post("/api/v1/courier/login");
        responseNonExistentUser.then().statusCode(404).body("message", equalTo("Учетная запись не найдена"));
        System.out.println(responseNonExistentUser.body().asString());
    }


}
