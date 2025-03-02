package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Get user data cases")
@Feature("Getting user data")
public class UserGetTest extends BaseTestCase {

    int testUserId = 2;

    String userUrl = "https://playground.learnqa.ru/api/user/";

    String loginUrl = "https://playground.learnqa.ru/api/user/login";

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test verifies the response fields when a user is not authorized")
    @DisplayName("Unauthorized user response verification")
    public void testGetUserDataNoAuth() {

        Response responseUserData = apiCoreRequests
                .makeGetRequest(userUrl + testUserId, null, null);

        String[] unexpectedFields = {"email", "firstName", "lastName"};

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFields);
    }


    @Test
    @Description("This test verifies the response fields when a user is authorized as themselves")
    @DisplayName("Authorized user response verification (same user)")
    public void testGetUserDataAuthAsSameUser() {

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.
                makePostRequest(loginUrl, authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");


        Response responseUserData = apiCoreRequests
                .makeGetRequest(userUrl + testUserId, header, cookie);

        String[] expectedFields = {"email", "username", "firstName", "lastName"};

        Assertions.assertJsonHasFields(responseUserData, expectedFields);


    }


    @Test
    @Description("This test verifies the response fields when a user is authorized as a different user")
    @DisplayName("Authorized user response verification (different user)")
    public void testGetUserDataAuthAsAnotherUser() {


        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response response = apiCoreRequests
                .makePostRequest(userUrl, userData);

        String userId = response.jsonPath().get("id");

        Map<String, String> authData = new HashMap<>();
        String email = userData.get("email");
        authData.put("email", email);
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(loginUrl, authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests
                .makeGetRequest(userUrl + testUserId, header, cookie);

        String[] unexpectedFields = {"email", "firstName", "lastName"};
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFields);
        Assertions.assertJsonHasField(responseUserData, "username");

        System.out.println("For email " + email + ", correct id will be: " + userId);


    }


}
