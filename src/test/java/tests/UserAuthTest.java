package tests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import lib.Assertions;
import static io.restassured.RestAssured.given;


public class UserAuthTest extends BaseTestCase {

    String cookie;
    String headers;
    int userId;

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseAuthData = given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        this.cookie = this.getCookie(responseAuthData,"auth_sid");
        this.headers = this.getHeader(responseAuthData, "x-csrf-token");
        this.userId = this.getIntFromJson(responseAuthData, "user_id");

    }


    @Test
    public void testUserAuth() {

        Response responseCheckAuth = given()
                .header("x-csrf-token", this.headers)
                .cookie("auth_sid", this.cookie)
                .get("https://playground.learnqa.ru/api/user/auth")
                .andReturn();
        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userId);

    }


    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition) {

        RequestSpecification spec = given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if (condition.equals("cookie")) {
            spec.cookie("auth_sid", this.cookie);
        } else if (condition.equals("headers")) {
            spec.header("x-csrf-token", this.headers);
        } else {
            throw new IllegalArgumentException(condition);
        }
        Response responseCheckAuth = spec.get().andReturn();
        Assertions.assertJsonByName(responseCheckAuth, "user_id", 0);

    }


}
