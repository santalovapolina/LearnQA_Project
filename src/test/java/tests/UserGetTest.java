package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.Constants.*;

@Epic("Get user data cases")
@Feature("Getting user data")
public class UserGetTest extends BaseTestCase {

    int testUserId = 2;

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Tag("Regress")
    @Description("This test verifies the response fields when a user is not authorized")
    @DisplayName("Unauthorized user response verification")
    public void testGetUserDataNoAuth() {

        Response responseUserData = apiCoreRequests
                .makeGetRequest(DEV_BASE_URL + USER_ENDPOINT + testUserId, null, null);

        String[] unexpectedFields = {"email", "firstName", "lastName"};

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFields);
    }


    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Regress")
    @Tag("Smoke")
    @Description("This test verifies the response fields when a user is authorized as themselves")
    @DisplayName("Authorized user response verification (same user)")
    public void testGetUserDataAuthAsSameUser() {

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.
                makePostRequest(DEV_BASE_URL + LOGIN_ENDPOINT, authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");


        Response responseUserData = apiCoreRequests
                .makeGetRequest(DEV_BASE_URL + USER_ENDPOINT + testUserId, header, cookie);

        String[] expectedFields = {"email", "username", "firstName", "lastName"};

        Assertions.assertJsonHasFields(responseUserData, expectedFields);


    }


    @Test
    @Severity(SeverityLevel.NORMAL)
    @Tag("Regress")
    @Description("This test verifies the response fields when a user is authorized as a different user")
    @DisplayName("Authorized user response verification (different user)")
    public void testGetUserDataAuthAsAnotherUser() {


        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response response = apiCoreRequests
                .makePostRequest(DEV_BASE_URL + USER_ENDPOINT, userData);

        String userId = response.jsonPath().get("id");

        Map<String, String> authData = new HashMap<>();
        String email = userData.get("email");
        authData.put("email", email);
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(DEV_BASE_URL + LOGIN_ENDPOINT, authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests
                .makeGetRequest(DEV_BASE_URL + USER_ENDPOINT + testUserId, header, cookie);

        String[] unexpectedFields = {"email", "firstName", "lastName"};
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFields);
        Assertions.assertJsonHasField(responseUserData, "username");

        System.out.println("For email " + email + ", correct id will be: " + userId);


    }


}
