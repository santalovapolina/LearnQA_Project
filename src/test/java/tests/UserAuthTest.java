package tests;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestCase;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.Map;
import lib.Assertions;
import org.junit.jupiter.api.DisplayName;

import static lib.Constants.*;

@Epic("Authorization cases")
@Feature("Authorization")
public class UserAuthTest extends BaseTestCase {

    String cookie;
    String headers;
    int userId;

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = URLtest;
    }

    @BeforeEach
    public void loginUser() {

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseAuthData = apiCoreRequests
                .makePostRequest(URLtest + APIDEVLoginUrl, authData);

        this.cookie = this.getCookie(responseAuthData, "auth_sid");
        this.headers = this.getHeader(responseAuthData, "x-csrf-token");
        this.userId = this.getIntFromJson(responseAuthData, "user_id");

    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Tag("Regress")
    @Tag("Smoke")
    @Description("This test verifies successful user authorization with valid credentials")
    @DisplayName("Test positive auth user")
    public void testUserAuth() {

        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest(URLtest + APIDEVAuthUrl, this.headers, this.cookie);

        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userId);

    }


    @Description("This test checks unauthorized access when either auth cookie or auth header is missing")
    @DisplayName("Test negative auth user with one of auth param")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Regress")
    @Tag("Smoke")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition) {

        if (condition.equals("cookie")) {
            Response responseCookie = apiCoreRequests.makeGetRequestWithCookie(URLtest + APIDEVAuthUrl, this.cookie);
            Assertions.assertJsonByName(responseCookie, "user_id", 0);
        } else if (condition.equals("headers")) {
            Response responseHeaders = apiCoreRequests.makeGetRequestWithToken(URLtest + APIDEVAuthUrl, this.headers);
            Assertions.assertJsonByName(responseHeaders, "user_id", 0);
        } else {
            throw new IllegalArgumentException(condition);
        }


    }


}
