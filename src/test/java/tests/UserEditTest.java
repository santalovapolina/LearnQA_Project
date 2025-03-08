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

@Epic("Edit user data cases")
@Feature("Editing user data")
public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    String userUrl = "https://playground.learnqa.ru/api/user/";

    String loginUrl = "https://playground.learnqa.ru/api/user/login";

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Regress")
    @Tag("Smoke")
    @Description("This test check successful new user editing")
    @DisplayName("Test positive editing new user")
    public void testEditJustCreatedUser() {
        //Генерация пользователя
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);

        Response response = apiCoreRequests
                .makePostRequest(userUrl, userData);

        String userId = response.jsonPath().get("id");

        //Авторизация пользователя
        Map<String, String> authData = new HashMap<>();
        String email = userData.get("email");
        authData.put("email", email);
        authData.put("password", userData.get("password"));

        Response responseAuthData = apiCoreRequests
                .makePostRequest(loginUrl, authData);

        String header = this.getHeader(responseAuthData, "x-csrf-token");
        String cookie = this.getCookie(responseAuthData, "auth_sid");

        //Редактирование пользователя
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = apiCoreRequests
                .makePutRequest(userUrl + userId, header, cookie, editData);


        //Получить информацию о пользователе
        Response responseUserData = apiCoreRequests
                .makeGetRequest(userUrl + userId, header, cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", newName);

    }


    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Regress")
    @Description("This test verifies the response text for unauthorized editing")
    @DisplayName("Test negative editing user unauthorized")
    public void testEditUserWhenUnauthorized() {

        //Генерация пользователя
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);

        Response response = apiCoreRequests
                .makePostRequest(userUrl, userData);

        String userId = response.jsonPath().get("id");

        //Редактирование пользователя
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = apiCoreRequests
                .makePutRequest(userUrl + userId, null, null, editData);

        Assertions.assertJsonHasField(responseEditUser, "error");
        Assertions.assertJsonByName(responseEditUser, "error", "Auth token not supplied");

    }


    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Regress")
    @Description("This test verifies the response text for editing another user data")
    @DisplayName("Test negative editing another user")
    public void testEditUserWhenAuthorizedAsAnotherUser() {

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

        String header = this.getHeader(responseAuthData, "x-csrf-token");
        String cookie = this.getCookie(responseAuthData, "auth_sid");

        //Редактирование пользователя 2
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = apiCoreRequests
                .makePutRequest(userUrl + secondUserId, header, cookie, editData);

        Assertions.assertJsonHasField(responseEditUser, "error");
        Assertions.assertJsonByName(responseEditUser, "error", "This user can only edit their own data.");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Tag("Regress")
    @Description("This test verifies the response text for invalid email format")
    @DisplayName("Test negative editing with invalid email value")
    public void testEditUserWithInvalidEmailFormat() {
        //Генерация пользователя
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);

        Response response = apiCoreRequests
                .makePostRequest(userUrl, userData);

        String userId = response.jsonPath().get("id");

        //Авторизация пользователя
        Map<String, String> authData = new HashMap<>();
        String email = userData.get("email");
        authData.put("email", email);
        authData.put("password", userData.get("password"));

        Response responseAuthData = apiCoreRequests
                .makePostRequest(loginUrl, authData);

        String header = this.getHeader(responseAuthData, "x-csrf-token");
        String cookie = this.getCookie(responseAuthData, "auth_sid");

        //Редактирование пользователя
        String newEmail = "polina_qa.example.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);
        Response responseEditUser = apiCoreRequests
                .makePutRequest(userUrl + userId, header, cookie, editData);

        Assertions.assertJsonHasField(responseEditUser, "error");
        Assertions.assertJsonByName(responseEditUser, "error", "Invalid email format");

    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Tag("Regress")
    @Description("This test verifies the response text for short firstName")
    @DisplayName("Test negative editing with short firstName")
    public void testEditUserWithShortName() {
        //Генерация пользователя
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);

        Response response = apiCoreRequests
                .makePostRequest(userUrl, userData);

        String userId = response.jsonPath().get("id");

        //Авторизация пользователя
        Map<String, String> authData = new HashMap<>();
        String email = userData.get("email");
        authData.put("email", email);
        authData.put("password", userData.get("password"));

        Response responseAuthData = apiCoreRequests
                .makePostRequest(loginUrl, authData);

        String header = this.getHeader(responseAuthData, "x-csrf-token");
        String cookie = this.getCookie(responseAuthData, "auth_sid");

        //Редактирование пользователя
        String newName = "l";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = apiCoreRequests
                .makePutRequest(userUrl + userId, header, cookie, editData);

        Assertions.assertJsonHasField(responseEditUser, "error");
        Assertions.assertJsonByName(responseEditUser, "error", "The value for field `firstName` is too short");

    }

}
