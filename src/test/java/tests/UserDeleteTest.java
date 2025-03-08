package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Delete user cases")
@Feature("User deletion")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    String deleteUrl = "https://playground.learnqa.ru/api/user/";

    String userUrl = "https://playground.learnqa.ru/api/user/";

    String loginUrl = "https://playground.learnqa.ru/api/user/login";

    String cookie;
    String headers;
    int userId;

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Tag("Regress")
    @Description("This test verifies the response when deleting reserved user")
    @DisplayName("Unsuccessful deletion of reserved user")
    public void testUnsuccessfulDeleteReservedUser() {

        //Авторизация пользователя
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseAuthData = apiCoreRequests
                .makePostRequest(loginUrl, authData);
        this.cookie = this.getCookie(responseAuthData, "auth_sid");
        this.headers = this.getHeader(responseAuthData, "x-csrf-token");
        this.userId = this.getIntFromJson(responseAuthData, "user_id");

        //Удаление пользователя
        Response responseCheckDelete = apiCoreRequests
                .makeDeleteRequest(deleteUrl + userId, this.headers, this.cookie);

        Assertions.assertJsonHasField(responseCheckDelete, "error");
        Assertions.assertJsonByName(responseCheckDelete, "error", "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

    }


    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Smoke")
    @Tag("Regress")
    @Description("This test verifies that a newly created user can be successfully deleted and cannot authorize after deletion")
    @DisplayName("Successful deletion of newly created user and login attempt verification")
    public void testSuccessfulDeleteJustCreatedUser() {

        //Генерация пользователя
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);

        Response response = apiCoreRequests
                .makePostRequest(userUrl, userData);
        String userId = response.jsonPath().get("id");

        //Авторизация пользователя
        Response responseAuthData = apiCoreRequests
                .makePostRequest(loginUrl, userData);
        this.cookie = this.getCookie(responseAuthData, "auth_sid");
        this.headers = this.getHeader(responseAuthData, "x-csrf-token");
        this.userId = this.getIntFromJson(responseAuthData, "user_id");


        //Удаление пользователя
        Response responseCheckDelete = apiCoreRequests
                .makeDeleteRequest(deleteUrl + userId, this.headers, this.cookie);

        //Повторная авторизация пользователя
        Response responseAuthAfterDeletion = apiCoreRequests
                .makePostRequest(loginUrl, userData);
        this.cookie = this.getCookie(responseAuthData, "auth_sid");
        this.headers = this.getHeader(responseAuthData, "x-csrf-token");
        this.userId = this.getIntFromJson(responseAuthData, "user_id");

        Assertions.assertInvalidLoginResponse(responseAuthAfterDeletion, "Invalid username/password supplied");
        Assertions.assertJsonHasField(responseCheckDelete, "success");
        Assertions.assertJsonByName(responseCheckDelete, "success", "!");

    }


    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Regress")
    @Description("This test verifies the response when deleting user authorized as another")
    @DisplayName("Unsuccessful deletion of unauthorized user")
    public void testDeleteUserAuthorizedAsAnother() {

        //Генерация пользователя 1
        Map<String, String> firstUserData = new HashMap<>();
        firstUserData = DataGenerator.getRegistrationData(firstUserData);

        Response responseFirstUser = apiCoreRequests
                .makePostRequest(userUrl, firstUserData);

        String firstUserId = responseFirstUser.jsonPath().get("id");

        //Генерация пользователя 2
        Map<String, String> secondUserData = new HashMap<>();
        secondUserData = DataGenerator.getRegistrationData(secondUserData);

        Response responseSecondUser = apiCoreRequests
                .makePostRequest(userUrl, secondUserData);

        String secondUserId = responseSecondUser.jsonPath().get("id");

        //Авторизация пользователя 1
        Map<String, String> authData = new HashMap<>();
        String email = firstUserData.get("email");
        authData.put("email", email);
        authData.put("password", firstUserData.get("password"));

        Response responseAuthData = apiCoreRequests
                .makePostRequest(loginUrl, authData);

        this.headers = this.getHeader(responseAuthData, "x-csrf-token");
        this.cookie = this.getCookie(responseAuthData, "auth_sid");
        this.userId = this.getIntFromJson(responseAuthData, "user_id");

        //Удаление пользователя
        Response responseCheckDelete = apiCoreRequests
                .makeDeleteRequest(deleteUrl + secondUserId, this.headers, this.cookie);

        Assertions.assertJsonHasField(responseCheckDelete, "error");
        Assertions.assertJsonByName(responseCheckDelete, "error", "This user can only delete their own account.");


    }


}
