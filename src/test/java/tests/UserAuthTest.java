package tests;
import io.restassured.response.Response;
import lib.BaseTestCase;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.Map;
import lib.Assertions;
import io.qameta.allure.Feature;
import io.qameta.allure.Epic;
import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;

@Epic("Authorization cases")
@Feature("Authorization")
public class UserAuthTest extends BaseTestCase {

    String cookie;
    String headers;
    int userId;

    String loginUrl = "https://playground.learnqa.ru/api/user/login";

    String authUrl = "https://playground.learnqa.ru/api/user/auth";

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();


    @BeforeEach
    public void loginUser() {

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseAuthData = apiCoreRequests
                .makePostRequest(loginUrl, authData);

        this.cookie = this.getCookie(responseAuthData, "auth_sid");
        this.headers = this.getHeader(responseAuthData, "x-csrf-token");
        this.userId = this.getIntFromJson(responseAuthData, "user_id");

    }

    @Test
    @Description("This test verifies successful user authorization with valid credentials")
    @DisplayName("Test positive auth user")
    public void testUserAuth() {

        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest(authUrl, this.headers, this.cookie);

        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userId);

    }


    @Description("This test checks unauthorized access when either auth cookie or auth header is missing")
    @DisplayName("Test negative auth user with one of auth param")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition) {

        if (condition.equals("cookie")) {
            Response responseCookie = apiCoreRequests.makeGetRequestWithCookie(authUrl, this.cookie);
            Assertions.assertJsonByName(responseCookie, "user_id", 0);
        } else if (condition.equals("headers")) {
            Response responseHeaders = apiCoreRequests.makeGetRequestWithToken(authUrl, this.headers);
            Assertions.assertJsonByName(responseHeaders, "user_id", 0);
        } else {
            throw new IllegalArgumentException(condition);
        }


    }


}
